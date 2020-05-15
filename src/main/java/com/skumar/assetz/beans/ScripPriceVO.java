package com.skumar.assetz.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class ScripPriceVO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String schemeName;
    private BigDecimal lastNav = BigDecimal.ZERO;
    private LocalDate lastNavDt;
    private LocalDate refNavDt;
    private BigDecimal navChange = BigDecimal.ZERO;
    private BigDecimal navChangePercent = BigDecimal.ZERO;

}
