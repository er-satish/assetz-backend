package com.skumar.assetz.beans;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TotalValuation implements Serializable{
    private static final long serialVersionUID = 1L;
    
    @JsonProperty("totalNetworth")
    private String networth;
    @JsonProperty("totalNetworthChange")
    private String change;

}
