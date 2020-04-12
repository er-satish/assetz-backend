package com.skumar.assetz.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skumar.assetz.repo.AssetsGenericRepo;
import com.skumar.assetz.service.PricingService;

@Service
public class PricingServiceImpl implements PricingService{
    
    @Autowired
    private AssetsGenericRepo assetsGenericRepo;

    @Override
    public Map<String, BigDecimal> getStocksPrice(LocalDate date, Set<String> isin) {
        return assetsGenericRepo.getCurrentNavForStocks(date, isin);
    }

    @Override
    public Map<String, BigDecimal> getMutualFundsPrice(LocalDate date, Set<String> isin) {
        // TODO Auto-generated method stub
        return new HashMap<>(0);
    }

}
