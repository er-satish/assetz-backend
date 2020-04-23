package com.skumar.assetz.service;

import java.time.LocalDate;

import com.skumar.assetz.beans.AssetsDetailsResponse;
import com.skumar.assetz.beans.AssetsSummaryResponse;

public interface AssetsService {
    
    AssetsSummaryResponse getAssetsSummary(LocalDate startDate,LocalDate endDate);

    AssetsDetailsResponse getAssetDetails(String assetType, String portfolioName, LocalDate startDate, LocalDate endDate);

}
