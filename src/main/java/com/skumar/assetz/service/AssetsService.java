package com.skumar.assetz.service;

import java.time.LocalDate;

import com.skumar.assetz.beans.AssetsSummaryResponse;

public interface AssetsService {
    
    AssetsSummaryResponse getAssetsSummary(LocalDate startDate,LocalDate endDate);

}
