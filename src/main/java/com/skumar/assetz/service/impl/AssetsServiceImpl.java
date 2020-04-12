package com.skumar.assetz.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
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
import com.skumar.assetz.beans.AssetsSummaryDetails;
import com.skumar.assetz.beans.AssetsSummaryHighlights;
import com.skumar.assetz.beans.AssetsSummaryResponse;
import com.skumar.assetz.beans.PortfolioSummary;
import com.skumar.assetz.dto.CurrentHoldingsDTO;
import com.skumar.assetz.repo.AssetsGenericRepo;
import com.skumar.assetz.service.AssetsService;
import com.skumar.assetz.service.PricingService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AssetsServiceImpl implements AssetsService {

    @Autowired
    private AssetsGenericRepo assetsGenericRepo;
    @Autowired
    private PricingService pricingService;

    private final String CURRENT_VALUATION = "Current Valuation";
    private final String STOCK = "Stock";
    private final String MUTUAL_FUND = "Mutual Fund";

    @Override
    public AssetsSummaryResponse getAssetsSummary() {
        log.info("going to fetch details");
        List<CurrentHoldingsDTO> results = assetsGenericRepo.getCurrentHoldings();
        updateCurrentValuations(results, findLastTradedDay());

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

    private LocalDate findLastTradedDay() {
        // TODO Auto-generated method stub
        //return LocalDate.now();
        return LocalDate.of(2020, 02, 03);
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
                    AssetsSummaryHighlights totalValuationForSelPf = new AssetsSummaryHighlights();//total current valuation for the selected portfolio
                    totalValuationForSelPf.setCardName(CURRENT_VALUATION);
                    assetsSummary.getHighlights().add(totalValuationForSelPf);
                    for (Entry<String, AssetsSummaryDetails> asEntry : assetsSummaryMap.entrySet()) {
                        assetsSummary.getAssetsSummaryDetails().add(asEntry.getValue());
                        totalValuationForSelPf.setCurrentValuation(totalValuationForSelPf.getCurrentValuation()
                                .add(asEntry.getValue().getCurrentValuation()));
                        //update total current valuation across all portfolios
                        response.getTotalValuation().setNetworth(response.getTotalValuation().getNetworth().add(totalValuationForSelPf.getCurrentValuation()));
                        
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

    private void updateCurrentValuations(List<CurrentHoldingsDTO> currentHoldings, LocalDate date) {
        // find unique ISIN and group based on asset type
        Map<String, Set<String>> uniqueIsin = new HashMap<>();
        if (!CollectionUtils.isEmpty(currentHoldings)) {
            for (CurrentHoldingsDTO ch : currentHoldings) {
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
        Map<String, BigDecimal> rates = new HashMap<>();
        // call respective service to get current price per unit
        if (!uniqueIsin.isEmpty()) {
            for (Entry<String, Set<String>> entry : uniqueIsin.entrySet()) {
                switch (entry.getKey()) {
                case STOCK:
                    // call stocks pricing service
                    rates.putAll(pricingService.getStocksPrice(date, entry.getValue()));
                    break;
                case MUTUAL_FUND:
                    // call MF pricing service
                    rates.putAll(pricingService.getMutualFundsPrice(date, entry.getValue()));
                    break;
                }
            }
        }

        // update current holdings based on rates received
        if (!CollectionUtils.isEmpty(currentHoldings)) {
            currentHoldings.forEach(ch -> updateCurrentValuation(ch, rates));
        }

    }

    private void updateCurrentValuation(CurrentHoldingsDTO ch, Map<String, BigDecimal> rates) {
        BigDecimal rate = rates.get(ch.getIsin());
        if (ch.getQuantity() != null && rate != null) {
            ch.setCurrentValuationAmt(ch.getQuantity().multiply(rate));
        }
    }

}
