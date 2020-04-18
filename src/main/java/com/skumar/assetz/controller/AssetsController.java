package com.skumar.assetz.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skumar.assetz.beans.AssetsSummaryResponse;
import com.skumar.assetz.beans.ValuationPeriod;
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
            @RequestParam(name = "valuationPeriod", required = false) ValuationPeriod valuationPeriod)
            throws IOException {
        log.info("Received request for getting assets summary at time: {} for ValuationPeriod: {}", LocalDateTime.now(),
                valuationPeriod);
        // String hardcoded = new String(Files.readAllBytes(new
        // File("/Users/~/mydata/lab/assetz/backend/assetz/src/main/resources/sampleRes.json").toPath()));
        // AssetsSummaryResponse response = new ObjectMapper().readValue(hardcoded,
        // AssetsSummaryResponse.class);
        return assetsService.getAssetsSummary(valuationPeriod);
    }

    @GetMapping("mfprice")
    public String fetchMfPrice() throws MalformedURLException, IOException {
        pricingService.populateMutualFundsPrice();
        return "Success";

    }

}
