package com.skumar.assetz.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PriceDTO{
    private String isin;
    private BigDecimal rate;
}
