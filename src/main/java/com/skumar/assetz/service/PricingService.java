package com.skumar.assetz.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

import com.skumar.assetz.dto.PriceDTO;

public interface PricingService {

    /**
     * Returns price of the stocks for the given date
     * 
     * @param date
     * @param isin
     * @return map of <ISIN, PriceDTO>
     */
    Map<String, PriceDTO> getStocksPrice(LocalDate date, Set<String> isin);

    /**
     * Returns price of the mutual funds for the given date
     * 
     * @param date
     * @param isin
     * @return map of <ISIN, PriceDTO>
     */
    Map<String, PriceDTO> getMutualFundsPrice(LocalDate date, Set<String> isin);
    
    /**
     * Returns price of the stocks for the given date
     * 
     * @param date
     * @param isin
     * @return map of <ISIN, PriceDTO>
     */
    Map<String, PriceDTO> getStocksPreviousPrice(LocalDate date, Set<String> isin);

    /**
     * Returns price of the mutual funds for the given date
     * 
     * @param date
     * @param isin
     * @return map of <ISIN, PriceDTO>
     */
    Map<String, PriceDTO> getMutualFundsPreviousPrice(LocalDate date, Set<String> isin);
    
    
    void populateMutualFundsPrice(LocalDate date) throws MalformedURLException, IOException;

    Map<String, PriceDTO> getEPFPrice(LocalDate endDate, Set<String> value);

    Map<String, PriceDTO> getPPFPrice(LocalDate endDate, Set<String> value);

}
