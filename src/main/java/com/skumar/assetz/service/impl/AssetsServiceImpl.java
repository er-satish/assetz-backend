package com.skumar.assetz.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private final String PPF = "PPF";
    private final String EPF = "EPF";

    @Override
    public AssetsSummaryResponse getAssetsSummary(LocalDate startDate, LocalDate endDate) {
        log.info("going to fetch details");
        List<CurrentHoldingsDTO> results = assetsGenericRepo.getCurrentHoldings();
        log.info("Count of fetched row:{}", results.size());
        updateValuations(results, startDate, endDate);

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

                if (dto.getPreviousValuationAmt() != null)
                    assetsSummaryDetails.setPreviousValuation(
                            assetsSummaryDetails.getPreviousValuation().add(dto.getPreviousValuationAmt()));

                // calculate loss-gain and notional loss-gain
                if (!BigDecimal.ZERO.equals(assetsSummaryDetails.getPreviousValuation())) {
                    assetsSummaryDetails.setGainLoss(assetsSummaryDetails.getCurrentValuation()
                            .subtract(assetsSummaryDetails.getPreviousValuation()));
                    assetsSummaryDetails
                            .setGainLossPercentage(assetsSummaryDetails.getGainLoss().multiply(BigDecimal.valueOf(100))
                                    .divide(assetsSummaryDetails.getPreviousValuation(), RoundingMode.HALF_UP));
                }

                assetsSummaryDetails.setTotalNotionalGainLoss(
                        assetsSummaryDetails.getCurrentValuation().subtract(assetsSummaryDetails.getAmountInvested()));
            }

        }

        // prepare the service response
        AssetsSummaryResponse response = buildAssetsSummaryResponse(portfolioMap);
        log.info("Assets valuation successfully completed");
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
                    /*
                     * total current valuation for the selected portfolio
                     */
                    AssetsSummaryHighlights totalValuation4SelectedPortfolio = new AssetsSummaryHighlights();
                    totalValuation4SelectedPortfolio.setCardName(CURRENT_VALUATION);
                    assetsSummary.getHighlights().add(totalValuation4SelectedPortfolio);
                    for (Entry<String, AssetsSummaryDetails> asEntry : assetsSummaryMap.entrySet()) {
                        assetsSummary.getAssetsSummaryDetails().add(asEntry.getValue());
                        totalValuation4SelectedPortfolio.setCurrentValuation(totalValuation4SelectedPortfolio
                                .getCurrentValuation().add(asEntry.getValue().getCurrentValuation()));
                        totalValuation4SelectedPortfolio.setPreviousValuation(totalValuation4SelectedPortfolio
                                .getPreviousValuation().add(asEntry.getValue().getPreviousValuation()));
                        if (!totalValuation4SelectedPortfolio.getPreviousValuation().equals(BigDecimal.ZERO)) {
                            totalValuation4SelectedPortfolio
                                    .setChange(totalValuation4SelectedPortfolio.getCurrentValuation()
                                            .subtract(totalValuation4SelectedPortfolio.getPreviousValuation()));
                            totalValuation4SelectedPortfolio.setChangePercentage(
                                    totalValuation4SelectedPortfolio.getChange().multiply(BigDecimal.valueOf(100))
                                            .divide(totalValuation4SelectedPortfolio.getPreviousValuation(),
                                                    RoundingMode.HALF_UP));
                        }
                        // update total current valuation across all portfolios
                        response.getTotalValuation().setNetworth(response.getTotalValuation().getNetworth()
                                .add(asEntry.getValue().getCurrentValuation()));
                        response.getTotalValuation().setPreviousNetworth(response.getTotalValuation()
                                .getPreviousNetworth().add(asEntry.getValue().getPreviousValuation()));
                        response.getTotalValuation().setChange(response.getTotalValuation().getNetworth()
                                .subtract(response.getTotalValuation().getPreviousNetworth()));

                        // prepare for other highlights data[Stocks and Mutual Funds]
                        if (asEntry.getValue().getAssetType().equalsIgnoreCase(STOCK)
                                || asEntry.getValue().getAssetType().equalsIgnoreCase(MUTUAL_FUND)) {
                            AssetsSummaryHighlights assetsSummaryHighlights = new AssetsSummaryHighlights();
                            assetsSummaryHighlights.setCardName(asEntry.getValue().getAssetType());
                            assetsSummaryHighlights.setCurrentValuation(asEntry.getValue().getCurrentValuation());
                            assetsSummaryHighlights.setPreviousValuation(asEntry.getValue().getPreviousValuation());
                            // mapping for change and change percentage
                            if (!BigDecimal.ZERO.equals(assetsSummaryHighlights.getPreviousValuation())) {
                                assetsSummaryHighlights.setChange(assetsSummaryHighlights.getCurrentValuation()
                                        .subtract(assetsSummaryHighlights.getPreviousValuation()));
                                assetsSummaryHighlights.setChangePercentage(
                                        assetsSummaryHighlights.getChange().multiply(BigDecimal.valueOf(100)).divide(
                                                assetsSummaryHighlights.getPreviousValuation(), RoundingMode.HALF_UP));
                            }
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

    private void updateValuations(List<CurrentHoldingsDTO> currentHoldings, LocalDate startDate, LocalDate endDate) {
        log.info("Going to calculate valuation for [startDate,endDate]: [{}, {}]", startDate, endDate);
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
                    rates.putAll(pricingService.getStocksPrice(endDate, entry.getValue()));
                    break;
                case MUTUAL_FUND:
                    // call MF pricing service
                    rates.putAll(pricingService.getMutualFundsPrice(endDate, entry.getValue()));
                    break;
                case PPF:
                    // for EPF pricing service
                    rates.putAll(pricingService.getPPFPrice(endDate, entry.getValue()));
                    break;
                case EPF:
                    // for PPF pricing service
                    rates.putAll(pricingService.getEPFPrice(endDate, entry.getValue()));
                    break;
                }
            }
        }
        Map<String, BigDecimal> previousPeriodRates = new HashMap<>();
        // call respective service to get previous period price per unit
        if (!uniqueIsin.isEmpty()) {
            for (Entry<String, Set<String>> entry : uniqueIsin.entrySet()) {
                switch (entry.getKey()) {
                case STOCK:
                    // call stocks pricing service
                    previousPeriodRates.putAll(pricingService.getStocksPreviousPrice(startDate, entry.getValue()));
                    break;
                case MUTUAL_FUND:
                    // call MF pricing service
                    previousPeriodRates.putAll(pricingService.getMutualFundsPreviousPrice(startDate, entry.getValue()));
                    break;
                case PPF:
                    // for EPF pricing service
                    previousPeriodRates.putAll(pricingService.getPPFPrice(endDate, entry.getValue()));
                    break;
                case EPF:
                    // for PPF pricing service
                    previousPeriodRates.putAll(pricingService.getEPFPrice(endDate, entry.getValue()));
                    break;
                }
            }
        }

        // update current holdings based on rates received
        if (!CollectionUtils.isEmpty(currentHoldings)) {
            currentHoldings.forEach(ch -> updateValuation(ch, rates, previousPeriodRates));
        }

    }

    private void updateValuation(CurrentHoldingsDTO ch, Map<String, BigDecimal> rates,
            Map<String, BigDecimal> previousPeriodRates) {
        BigDecimal rate = rates.get(ch.getIsin());
        if (ch.getQuantity() != null && rate != null) {
            ch.setCurrentValuationAmt(ch.getQuantity().multiply(rate));
        }

        BigDecimal previousRate = previousPeriodRates.get(ch.getIsin());
        if (ch.getQuantity() != null && previousRate != null) {
            ch.setPreviousValuationAmt(ch.getQuantity().multiply(previousRate));
        }
    }

}
