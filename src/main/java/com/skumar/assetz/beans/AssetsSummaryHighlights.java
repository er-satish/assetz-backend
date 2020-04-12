package com.skumar.assetz.beans;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AssetsSummaryHighlights implements Serializable{
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @JsonProperty("name")
    private String cardName;
    
    @JsonProperty("amount")
    private BigDecimal currentValuation = BigDecimal.ZERO;
    
    private BigDecimal changePercentage = BigDecimal.ZERO;
    
    private BigDecimal change = BigDecimal.ZERO;

}
