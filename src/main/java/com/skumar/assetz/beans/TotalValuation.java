package com.skumar.assetz.beans;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TotalValuation implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("totalNetworth")
    private BigDecimal networth = BigDecimal.ZERO;
    @JsonProperty("totalNetworthChange")
    private BigDecimal change = BigDecimal.ZERO;
    
    private BigDecimal previousNetworth = BigDecimal.ZERO;

}
