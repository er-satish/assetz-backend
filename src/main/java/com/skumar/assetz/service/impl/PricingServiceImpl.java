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
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.skumar.assetz.dto.PriceDTO;
import com.skumar.assetz.entity.MfNavHistory;
import com.skumar.assetz.repo.AssetsGenericRepo;
import com.skumar.assetz.repo.MfNavHistoryRepo;
import com.skumar.assetz.service.PricingService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PricingServiceImpl implements PricingService {
    final static String url = "https://www.amfiindia.com/spages/NAVOpen.txt";
    final static String mountPoint = "/home/pi/lab/myapp/assetz/backend/data/";
    final static String datePattern = "d-MMM-uuuu";

    @Autowired
    private AssetsGenericRepo assetsGenericRepo;

    @Autowired
    private MfNavHistoryRepo mfNavHistoryRepo;

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

}
