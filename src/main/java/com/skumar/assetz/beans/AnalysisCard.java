package com.skumar.assetz.beans;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AnalysisCard implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private String name;
    private LocalDate fromDate;
    private LocalDate toDate;  
    @JsonProperty("details")
    private List<ScripPriceVO> scripPriceVOs;
    

}
