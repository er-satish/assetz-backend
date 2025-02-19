package com.skumar.assetz.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AssetsSummary implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private List<AssetsSummaryHighlights> highlights = new ArrayList<>();
    
    @JsonProperty("details")
    private List<AssetsSummaryDetails> assetsSummaryDetails = new ArrayList<>();

    

}
