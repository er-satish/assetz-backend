package com.skumar.assetz.beans;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Summary details of single type of portfolio. e.g. Retirement, Emergency Fund
 * etc.
 * 
 * @author skumar
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioSummary implements Serializable {
    /**
    * 
    */
    private static final long serialVersionUID = 1L;
    
    private String portfolioName;
    @JsonProperty("assets")
    private AssetsSummary assetsSummary;
    
}
