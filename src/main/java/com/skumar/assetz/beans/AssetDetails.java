package com.skumar.assetz.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class AssetDetails implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String schemeName;
    private BigDecimal lastNav = BigDecimal.ZERO;
    private LocalDate lastNavDt;
    private BigDecimal navChange = BigDecimal.ZERO;
    private BigDecimal navChangePercent = BigDecimal.ZERO;
    private BigDecimal units = BigDecimal.ZERO;
    private BigDecimal avgCost = BigDecimal.ZERO;
    private BigDecimal investedAmt = BigDecimal.ZERO;
    private BigDecimal gainLoss = BigDecimal.ZERO;
    private BigDecimal gainLossPercent = BigDecimal.ZERO;
    private BigDecimal notionalGainLoss = BigDecimal.ZERO;
    private BigDecimal notionalGainLossPercent = BigDecimal.ZERO;
    private BigDecimal currentValuation = BigDecimal.ZERO;
    private BigDecimal previousValuation = BigDecimal.ZERO;

}
