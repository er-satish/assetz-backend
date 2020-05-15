package com.skumar.assetz.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AnalysisResult implements Serializable {

private static final long serialVersionUID = 1L;
    
    @JsonProperty("cardsData")
    private List<AnalysisCard> analysisCards = new ArrayList<>();

}
