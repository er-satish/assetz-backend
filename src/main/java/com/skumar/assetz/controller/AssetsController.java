package com.skumar.assetz.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skumar.assetz.beans.AnalysisResult;
import com.skumar.assetz.beans.AssetsDetailsResponse;
import com.skumar.assetz.beans.AssetsSummaryResponse;
import com.skumar.assetz.entity.BillPayment;
import com.skumar.assetz.service.AssetsService;
import com.skumar.assetz.service.PricingService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@CrossOrigin("*")
public class AssetsController {

    @Autowired
    private AssetsService assetsService;

    @Autowired
    private PricingService pricingService;

    @GetMapping("hello")
    public String hello() {
        return "Hi..i'm, availabe at your service.";
    }

    @GetMapping("assets")
    public AssetsSummaryResponse getAssetsSummary(
            @RequestParam(name = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate)
            throws IOException {
        log.info("Received request for getting assets summary at time: {} for [startDate, endDate]: [{}, {}]", LocalDateTime.now(),
                startDate,endDate);
        return assetsService.getAssetsSummary(startDate,endDate);
    }

    @GetMapping("mfprice/{date}")
    public String fetchMfPrice(@PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) throws MalformedURLException, IOException {
        pricingService.populateMutualFundsPrice(date);
        return "Success";
    }
    
    @GetMapping("assets/types/{assetType}/portfolios/{portfolioName}")
    public AssetsDetailsResponse getAssetDetails(
            @PathVariable("assetType") String assetType,
            @PathVariable("portfolioName") String portfolioName,
            @RequestParam(name = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate)
            throws IOException {
        log.info("Received request for getting asset details at time: {} for [assetType, portfolioName, startDate, endDate]: [{}, {}, {}, {}]", assetType, portfolioName, LocalDateTime.now(),
                startDate,endDate);
        return assetsService.getAssetDetails(assetType,portfolioName,startDate,endDate);
    }
    
    @GetMapping("bills")
    public List<BillPayment> fetchBills() throws MalformedURLException, IOException {
        return pricingService.getBills();
    }
    
    @PutMapping("bills")
    public List<BillPayment> updateBills(@RequestBody @Valid List<BillPayment> bills) throws MalformedURLException, IOException {
        return pricingService.saveBills(bills);
    }
    
    @GetMapping("analysis")
    public AnalysisResult getAnalysisResult() throws MalformedURLException, IOException {
        return pricingService.getAnalysisResult();
    }

}
