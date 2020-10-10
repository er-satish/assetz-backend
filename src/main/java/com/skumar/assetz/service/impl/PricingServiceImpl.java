package com.skumar.assetz.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.skumar.assetz.beans.AnalysisCard;
import com.skumar.assetz.beans.AnalysisResult;
import com.skumar.assetz.beans.ScripPriceVO;
import com.skumar.assetz.dto.PriceDTO;
import com.skumar.assetz.entity.BillPayment;
import com.skumar.assetz.entity.MfNavHistory;
import com.skumar.assetz.repo.AssetsGenericRepo;
import com.skumar.assetz.repo.BillPaymentRepo;
import com.skumar.assetz.repo.MfNavHistoryRepo;
import com.skumar.assetz.service.PricingService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PricingServiceImpl implements PricingService {
    final static String url = "https://www.amfiindia.com/spages/NAVOpen.txt";
    final static String mountPoint = "./";
    final static String datePattern = "d-MMM-uuuu";

    @Autowired
    private AssetsGenericRepo assetsGenericRepo;

    @Autowired
    private MfNavHistoryRepo mfNavHistoryRepo;
    
    @Autowired
    private BillPaymentRepo billPaymentRepo;

    @Override
    public Map<String, PriceDTO> getStocksPrice(LocalDate date, Set<String> isin) {
        return assetsGenericRepo.getNavForStocks(date, isin);
    }

    @Override
    public Map<String, PriceDTO> getMutualFundsPrice(LocalDate date, Set<String> isin) {
        return assetsGenericRepo.getNavForMF(date, isin);
    }

    @Override
    public Map<String, PriceDTO> getStocksPreviousPrice(LocalDate date, Set<String> isin) {
        return assetsGenericRepo.getPreviousNavForStocks(date, isin);
    }

    @Override
    public Map<String, PriceDTO> getMutualFundsPreviousPrice(LocalDate date, Set<String> isin) {
        return assetsGenericRepo.getPreviousNavForMF(date, isin);
    }

    @Override
    public void populateMutualFundsPrice(LocalDate navDate) throws MalformedURLException, IOException {
        log.info("Going to download prices from : {}", url);
        String navDateFormatted = navDate.format(DateTimeFormatter.ofPattern(datePattern));
        log.info("Prices will be populated for day:{}", navDateFormatted);
        final Pattern regexPattern = Pattern.compile(navDateFormatted + "$");

        String fileName = mountPoint + navDateFormatted;
        InputStream in = new URL(url).openStream();
        Files.copy(in, Paths.get(fileName), StandardCopyOption.REPLACE_EXISTING);
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)))) {
            reader.lines().filter(line -> regexPattern.matcher(line).find()).forEach(c -> doX(c, navDate));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void doX(String c, LocalDate navDate) {
        String[] arr = c.split(";");

        if (arr.length == 6) {
            MfNavHistory mfNavHistory = new MfNavHistory();
            mfNavHistory.setIsin(arr[1]);
            mfNavHistory.setSymbol(arr[3]);
            mfNavHistory.setClose(new BigDecimal(arr[4]));
            mfNavHistory.setTimestamp(navDate);

            log.info("Going to save prices with details:{}", mfNavHistory);
            try {
                mfNavHistoryRepo.save(mfNavHistory);
            } catch (Exception e) {
                log.error("failed to save nav for mf:{}", mfNavHistory);
            }

        }

    }

    @Override
    public Map<String, PriceDTO> getEPFPrice(LocalDate endDate, Set<String> value) {
        // TODO impl. actual logic
        Map<String, PriceDTO> ratesMap = new HashMap<String, PriceDTO>();
        if (!CollectionUtils.isEmpty(value)) {
            value.forEach(v -> ratesMap.put(v, PriceDTO.builder().rate(BigDecimal.ONE).build()));
        }
        return ratesMap;
    }

    @Override
    public Map<String, PriceDTO> getPPFPrice(LocalDate endDate, Set<String> value) {
        Map<String, PriceDTO> ratesMap = new HashMap<String, PriceDTO>();
        // TODO impl. actual logic
        if (!CollectionUtils.isEmpty(value)) {
            value.forEach(v -> ratesMap.put(v, PriceDTO.builder().rate(BigDecimal.valueOf(1.4212)).build()));

        }
        return ratesMap;
    }

    @Override
    public List<BillPayment> getBills() {
        return billPaymentRepo.findAll();
    }

    @Override
    public List<BillPayment> saveBills(List<BillPayment> bills) {
        return billPaymentRepo.saveAll(bills);
    }

    @Override
    public AnalysisResult getAnalysisResult() {
        AnalysisResult analysisResult = new AnalysisResult();

        //get stocks based on last trading day change percentage
        AnalysisCard analysisCard = new AnalysisCard();
        analysisCard.setName("Last Trading Day");
        List<ScripPriceVO> scripPriceVOs = assetsGenericRepo.getVolatileStocks();
        analysisCard.setScripPriceVOs(scripPriceVOs);
        if(!CollectionUtils.isEmpty(scripPriceVOs)) {
            analysisCard.setFromDate(scripPriceVOs.get(0).getLastNavDt());
            analysisCard.setToDate(scripPriceVOs.get(0).getLastNavDt());
        }
        analysisResult.getAnalysisCards().add(analysisCard);
        
        //get stocks based on last 7 trading days change percentage
        AnalysisCard analysisCard1 = new AnalysisCard();
        analysisCard1.setName("Last 7 Trading Days");
        List<ScripPriceVO> scripPriceVOs1 = assetsGenericRepo.getVolatileStocksForPeriod(7);
        analysisCard1.setScripPriceVOs(scripPriceVOs1);
        if(!CollectionUtils.isEmpty(scripPriceVOs1)) {
            analysisCard1.setFromDate(scripPriceVOs1.get(0).getRefNavDt());
            analysisCard1.setToDate(scripPriceVOs1.get(0).getLastNavDt());
        }
        analysisResult.getAnalysisCards().add(analysisCard1);
        
        //get stocks based on last 14 trading days change percentage
        AnalysisCard analysisCard2 = new AnalysisCard();
        analysisCard2.setName("Last 14 Trading Days");
        List<ScripPriceVO> scripPriceVOs2 = assetsGenericRepo.getVolatileStocksForPeriod(14);
        analysisCard2.setScripPriceVOs(scripPriceVOs2);
        if(!CollectionUtils.isEmpty(scripPriceVOs2)) {
            analysisCard2.setFromDate(scripPriceVOs2.get(0).getRefNavDt());
            analysisCard2.setToDate(scripPriceVOs2.get(0).getLastNavDt());
        }
        analysisResult.getAnalysisCards().add(analysisCard2);
        
      //get stocks based on last 28 trading days change percentage
        AnalysisCard analysisCard3 = new AnalysisCard();
        analysisCard3.setName("Last 28 Trading Days");
        List<ScripPriceVO> scripPriceVOs3 = assetsGenericRepo.getVolatileStocksForPeriod(28);
        analysisCard3.setScripPriceVOs(scripPriceVOs3);
        if(!CollectionUtils.isEmpty(scripPriceVOs3)) {
            analysisCard3.setFromDate(scripPriceVOs3.get(0).getRefNavDt());
            analysisCard3.setToDate(scripPriceVOs3.get(0).getLastNavDt());
        }
        analysisResult.getAnalysisCards().add(analysisCard3);
        
        
        return analysisResult;
    }

}
