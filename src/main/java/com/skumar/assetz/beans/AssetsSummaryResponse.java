package com.skumar.assetz.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class AssetsSummaryResponse implements Serializable{
    private static final long serialVersionUID = 1L;
    
    @JsonProperty("data")
    private List<PortfolioSummary> portfolioSummary = new ArrayList<>();
    
    @JsonProperty("total")
    private TotalValuation totalValuation = new TotalValuation();
    

    

}
