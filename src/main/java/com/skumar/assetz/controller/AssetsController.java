package com.skumar.assetz.controller;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skumar.assetz.beans.AssetsSummaryResponse;
import com.skumar.assetz.service.AssetsService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@CrossOrigin("*")
public class AssetsController {

    @Autowired
    private AssetsService assetsService;
    
    @GetMapping("hello")
    public String hello() {
        return "Hi..i'm, availabe at your service.";
    }

    @GetMapping("assets")
    public AssetsSummaryResponse getAssetsSummary() throws IOException {
        log.info("Received request for getting assets summary at time: {}", LocalDateTime.now());
        //String hardcoded = new String(Files.readAllBytes(new File("/Users/skuma596/mydata/lab/assetz/backend/assetz/src/main/resources/sampleRes.json").toPath()));
        //AssetsSummaryResponse   response = new ObjectMapper().readValue(hardcoded, AssetsSummaryResponse.class);
        return assetsService.getAssetsSummary();
    }

}
