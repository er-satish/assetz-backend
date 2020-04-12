package com.skumar.assetz.beans;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

@Data
public class AssetsSummaryDetails implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * Stock, Mutual Fund etc.
     */
    private String assetType;
    private BigDecimal currentValuation = BigDecimal.ZERO;
    private BigDecimal amountInvested = BigDecimal.ZERO;
    private BigDecimal gainLoss = BigDecimal.ZERO;
    private BigDecimal gainLossPercentage = BigDecimal.ZERO;
    private BigDecimal totalNotionalGainLoss = BigDecimal.ZERO;
    private BigDecimal totalRealizedGainLoss = BigDecimal.ZERO;
}
