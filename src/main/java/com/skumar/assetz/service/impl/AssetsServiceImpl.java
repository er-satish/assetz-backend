package com.skumar.assetz.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.skumar.assetz.beans.AssetsSummary;
import com.skumar.assetz.beans.AssetsSummaryResponse;
import com.skumar.assetz.beans.PortfolioSummary;
import com.skumar.assetz.beans.AssetsSummaryDetails;
import com.skumar.assetz.beans.AssetsSummaryHighlights;
import com.skumar.assetz.dto.CurrentHoldingsDTO;
import com.skumar.assetz.repo.AssetsGenericRepo;
import com.skumar.assetz.service.AssetsService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AssetsServiceImpl implements AssetsService {

    @Autowired
    private AssetsGenericRepo assetsGenericRepo;

    private final String CURRENT_VALUATION = "Current Valuation";
    private final String STOCK = "Stock";
    private final String MUTUAL_FUND = "Mutual Fund";

    @Override
    public AssetsSummaryResponse getAssetsSummary() {
        log.info("going to fetch details");
        List<CurrentHoldingsDTO> results = assetsGenericRepo.getCurrentHoldings();
        updateCurrentValuations(results);

        Map<String, Map<String, AssetsSummaryDetails>> portfolioMap = new HashMap<>();

        if (!CollectionUtils.isEmpty(results)) {
            for (CurrentHoldingsDTO dto : results) {
                Map<String, AssetsSummaryDetails> assetsSummaryMap = null;
                if (portfolioMap.containsKey(dto.getPortfolioName())) {
                    assetsSummaryMap = portfolioMap.get(dto.getPortfolioName());
                } else {
                    assetsSummaryMap = new HashMap<String, AssetsSummaryDetails>();
                    portfolioMap.put(dto.getPortfolioName(), assetsSummaryMap);
                }
                AssetsSummaryDetails assetsSummaryDetails = null;
                if (assetsSummaryMap.containsKey(dto.getAssetType())) {
                    assetsSummaryDetails = assetsSummaryMap.get(dto.getAssetType());
                } else {
                    assetsSummaryDetails = new AssetsSummaryDetails();
                    assetsSummaryDetails.setAssetType(dto.getAssetType());
                    assetsSummaryMap.put(dto.getAssetType(), assetsSummaryDetails);
                }

                // aggregate amounts, profit/loss etc. based for a specific asset type
                if (dto.getInvestedAmt() != null)
                    assetsSummaryDetails
                            .setAmountInvested(assetsSummaryDetails.getAmountInvested().add(dto.getInvestedAmt()));

                if (dto.getCurrentValuationAmt() != null)
                    assetsSummaryDetails.setCurrentValuation(
                            assetsSummaryDetails.getCurrentValuation().add(dto.getCurrentValuationAmt()));
            }

            // TODO calculate the profit and loss

        }

        AssetsSummaryResponse response = buildAssetsSummaryResponse(portfolioMap);
        // prepare the service response

        log.info("Count of fetched row:{}", results.size());
        return response;
    }

    private AssetsSummaryResponse buildAssetsSummaryResponse(
            Map<String, Map<String, AssetsSummaryDetails>> portfolioMap) {
        AssetsSummaryResponse response = new AssetsSummaryResponse();
        if (!portfolioMap.isEmpty()) {
            for (Entry<String, Map<String, AssetsSummaryDetails>> entry : portfolioMap.entrySet()) {
                PortfolioSummary ps = new PortfolioSummary();
                ps.setPortfolioName(entry.getKey());

                AssetsSummary assetsSummary = new AssetsSummary();
                Map<String, AssetsSummaryDetails> assetsSummaryMap = entry.getValue();
                if (!assetsSummaryMap.isEmpty()) {
                    AssetsSummaryHighlights totalCurrentValuation = new AssetsSummaryHighlights();
                    totalCurrentValuation.setCardName(CURRENT_VALUATION);
                    assetsSummary.getHighlights().add(totalCurrentValuation);
                    for (Entry<String, AssetsSummaryDetails> asEntry : assetsSummaryMap.entrySet()) {
                        assetsSummary.getAssetsSummaryDetails().add(asEntry.getValue());
                        totalCurrentValuation.setCurrentValuation(totalCurrentValuation.getCurrentValuation()
                                .add(asEntry.getValue().getCurrentValuation()));
                        // TODO add logic for change and change percentage
                        // prepare for highlights data
                        if (asEntry.getValue().getAssetType().equalsIgnoreCase(STOCK)
                                || asEntry.getValue().getAssetType().equalsIgnoreCase(MUTUAL_FUND)) {
                            AssetsSummaryHighlights assetsSummaryHighlights = new AssetsSummaryHighlights();
                            assetsSummaryHighlights.setCardName(asEntry.getValue().getAssetType());
                            assetsSummaryHighlights.setCurrentValuation(asEntry.getValue().getCurrentValuation());
                            // TODO mapping for change and change percentage
                            // assetsSummaryHighlights.setChange(change);
                            assetsSummary.getHighlights().add(assetsSummaryHighlights);

                        }
                    }
                }

                ps.setAssetsSummary(assetsSummary);
                response.getPortfolioSummary().add(ps);
            }
        }
        return response;
    }

    private void updateCurrentValuations(List<CurrentHoldingsDTO> results) {
        // find unique ISIN and group based on asset type
        Map<String, Set<String>> uniqueIsin = new HashMap<>();
        if (results != null && !results.isEmpty()) {
            for (CurrentHoldingsDTO ch : results) {
                Set<String> isinSet = null;
                if (uniqueIsin.containsKey(ch.getAssetType())) {
                    isinSet = uniqueIsin.get(ch.getAssetType());
                } else {
                    isinSet = new HashSet<>();
                    uniqueIsin.put(ch.getAssetType(), isinSet);
                }
                isinSet.add(ch.getIsin());
            }
        }
        
        //call respective service to get current price per unit
        for(Entry<String, Set<String>> entry: uniqueIsin.entrySet()) {
            switch(entry.getKey()) {
            case STOCK:
                //call stock service
                break;
            case  MUTUAL_FUND:
                //call MF service
                break;
            }
            
        }
        
    }

}
