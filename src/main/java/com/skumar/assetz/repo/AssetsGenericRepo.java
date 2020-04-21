package com.skumar.assetz.repo;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.skumar.assetz.dto.CurrentHoldingsDTO;
import com.skumar.assetz.dto.PriceDTO;

@Repository
public class AssetsGenericRepo {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final String queryToGetCurrentHoldings = "select portfolio_name,asset_type,isin,scrip_name,avg_rate,quantity,invested_amt \n"
            + "from current_holding order by portfolio_name,asset_type,isin";

    private final String queryToGetCurrentNavForStocks = "select isin,close from ( select isin,close,timestamp,ROW_NUMBER() over (partition by isin order by timestamp desc) as rn from public.navhistory nh where nh.isin in (:isin) and nh.timestamp<=:date ) tem where tem.rn=1";

    private final String queryToGetCurrentNavForMf = "select isin,close from ( select isin,close,timestamp,ROW_NUMBER() over (partition by isin order by timestamp desc) as rn from public.mfnavhistory nh where nh.isin in (:isin) and nh.timestamp<=:date ) tem where tem.rn=1";

    private final String queryToGetPreviousNavForStocks = "select isin,close from ( select isin,close,timestamp,ROW_NUMBER() over (partition by isin order by timestamp asc) as rn from public.navhistory nh where nh.isin in ( :isin ) and timestamp>=:date ) tem where tem.rn=1";

    private final String queryToGetPreviousNavForMf = "select isin,close from ( select isin,close,timestamp,ROW_NUMBER() over (partition by isin order by timestamp asc) as rn from public.mfnavhistory nh where nh.isin in ( :isin ) and timestamp>=:date ) tem where tem.rn=1";

    public List<CurrentHoldingsDTO> getCurrentHoldings() {

        return jdbcTemplate.query(queryToGetCurrentHoldings, new RowMapper<CurrentHoldingsDTO>() {

            @Override
            public CurrentHoldingsDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                CurrentHoldingsDTO dto = new CurrentHoldingsDTO();
                dto.setPortfolioName(rs.getString("portfolio_name"));
                dto.setAssetType(rs.getString("asset_type"));
                dto.setIsin(rs.getString("isin"));
                dto.setScripName(rs.getString("scrip_name"));
                dto.setAvgRate(rs.getBigDecimal("avg_rate"));
                dto.setQuantity(rs.getBigDecimal("quantity"));
                dto.setInvestedAmt(rs.getBigDecimal("invested_amt"));
                return dto;
            }

        });
    }

    public Map<String, BigDecimal> getNavForStocks(LocalDate date, Set<String> isin) {
        Map<String, BigDecimal> ratesMap = new HashMap<String, BigDecimal>();

        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("date", date).addValue("isin", isin);

        List<PriceDTO> rates = namedParameterJdbcTemplate.query(queryToGetCurrentNavForStocks, namedParameters,
                new RowMapper<PriceDTO>() {

                    @Override
                    public PriceDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                        PriceDTO price = new PriceDTO();
                        price.setIsin(rs.getString("isin"));
                        price.setRate(rs.getBigDecimal("close"));
                        return price;
                    }

                });

        if (!CollectionUtils.isEmpty(rates)) {
            rates.stream().forEach(pdto -> {
                ratesMap.put(pdto.getIsin(), pdto.getRate());
            });
        }
        return ratesMap;
    }

    public Map<String, BigDecimal> getPreviousNavForStocks(LocalDate date, Set<String> isin) {
        Map<String, BigDecimal> ratesMap = new HashMap<String, BigDecimal>();

        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("date", date).addValue("isin", isin);

        List<PriceDTO> rates = namedParameterJdbcTemplate.query(queryToGetPreviousNavForStocks, namedParameters,
                new RowMapper<PriceDTO>() {

                    @Override
                    public PriceDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                        PriceDTO price = new PriceDTO();
                        price.setIsin(rs.getString("isin"));
                        price.setRate(rs.getBigDecimal("close"));
                        return price;
                    }

                });

        if (!CollectionUtils.isEmpty(rates)) {
            rates.stream().forEach(pdto -> {
                ratesMap.put(pdto.getIsin(), pdto.getRate());
            });
        }
        return ratesMap;
    }

    public Map<String, BigDecimal> getNavForMF(LocalDate date, Set<String> isin) {
        Map<String, BigDecimal> ratesMap = new HashMap<String, BigDecimal>();

        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("date", date).addValue("isin", isin);

        List<PriceDTO> rates = namedParameterJdbcTemplate.query(queryToGetCurrentNavForMf, namedParameters,
                new RowMapper<PriceDTO>() {

                    @Override
                    public PriceDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                        PriceDTO price = new PriceDTO();
                        price.setIsin(rs.getString("isin"));
                        price.setRate(rs.getBigDecimal("close"));
                        return price;
                    }

                });

        if (!CollectionUtils.isEmpty(rates)) {
            rates.stream().forEach(pdto -> {
                ratesMap.put(pdto.getIsin(), pdto.getRate());
            });
        }
        return ratesMap;
    }

    public Map<String, BigDecimal> getPreviousNavForMF(LocalDate date, Set<String> isin) {
        Map<String, BigDecimal> ratesMap = new HashMap<String, BigDecimal>();

        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("date", date).addValue("isin", isin);

        List<PriceDTO> rates = namedParameterJdbcTemplate.query(queryToGetPreviousNavForMf, namedParameters,
                new RowMapper<PriceDTO>() {

                    @Override
                    public PriceDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                        PriceDTO price = new PriceDTO();
                        price.setIsin(rs.getString("isin"));
                        price.setRate(rs.getBigDecimal("close"));
                        return price;
                    }

                });

        if (!CollectionUtils.isEmpty(rates)) {
            rates.stream().forEach(pdto -> {
                ratesMap.put(pdto.getIsin(), pdto.getRate());
            });
        }
        return ratesMap;
    }

}
