package com.skumar.assetz.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

public interface PricingService {

    /**
     * Returns price of the stocks for the given date
     * 
     * @param date
     * @param isin
     * @return map of <ISIN, Price>
     */
    Map<String, BigDecimal> getStocksPrice(LocalDate date, Set<String> isin);

    /**
     * Returns price of the mutual funds for the given date
     * 
     * @param date
     * @param isin
     * @return map of <ISIN, Price>
     */
    Map<String, BigDecimal> getMutualFundsPrice(LocalDate date, Set<String> isin);

}
