package com.skumar.assetz.service;

import com.skumar.assetz.beans.AssetsSummaryResponse;
import com.skumar.assetz.beans.ValuationPeriod;

public interface AssetsService {
    
    AssetsSummaryResponse getAssetsSummary(ValuationPeriod valuationPeriod);

}
