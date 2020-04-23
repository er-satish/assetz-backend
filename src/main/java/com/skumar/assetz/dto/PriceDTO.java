package com.skumar.assetz.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceDTO{
    private String isin;
    /**
     * Nav value of the isin
     */
    private BigDecimal rate;

    /**
     * Date on which the nav is chosen for the isin
     */
    private LocalDate navDt;
}
