package com.skumar.assetz.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentHoldingsDTO implements Serializable{
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private String portfolioName;
    private String assetType;
    private String isin;
    private String scripName;
    /**
     * Average purchase price for the scrip/isin 
     */
    private BigDecimal avgRate;
    private BigDecimal quantity;
    private BigDecimal investedAmt;
    private BigDecimal currentValuationAmt;
    /**
     * Previous valuation amount could be based on last day, this week etc. depending on user selected date
     */
    private BigDecimal previousValuationAmt;
    
    /**
     * Nav as per the selected end-date
     */
    private BigDecimal lastNav = BigDecimal.ZERO;
    private LocalDate lastNavDt;
    
    /**
     * Nav as per the selected start-date
     */
    private BigDecimal previousNav = BigDecimal.ZERO;
    private LocalDate previousNavDt;
 
}
