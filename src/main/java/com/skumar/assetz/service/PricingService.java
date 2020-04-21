package com.skumar.assetz.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
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
    
    /**
     * Returns price of the stocks for the given date
     * 
     * @param date
     * @param isin
     * @return map of <ISIN, Price>
     */
    Map<String, BigDecimal> getStocksPreviousPrice(LocalDate date, Set<String> isin);

    /**
     * Returns price of the mutual funds for the given date
     * 
     * @param date
     * @param isin
     * @return map of <ISIN, Price>
     */
    Map<String, BigDecimal> getMutualFundsPreviousPrice(LocalDate date, Set<String> isin);
    
    
    void populateMutualFundsPrice(LocalDate date) throws MalformedURLException, IOException;

    Map<String, BigDecimal> getEPFPrice(LocalDate endDate, Set<String> value);

    Map<String, BigDecimal> getPPFPrice(LocalDate endDate, Set<String> value);

}
