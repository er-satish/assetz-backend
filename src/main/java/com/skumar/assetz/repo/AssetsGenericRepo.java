package com.skumar.assetz.repo;

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

import com.skumar.assetz.beans.ScripPriceVO;
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
    
    private final String queryGetHoldingsByAssetTypeAndPortfolio = "select portfolio_name,asset_type,isin,scrip_name,avg_rate,quantity,invested_amt \n"
            + "from current_holding where portfolio_name=:portfolioName and asset_type=:assetType order by isin";

    private final String queryToGetCurrentNavForStocks = "select isin,close,timestamp from ( select isin,close,timestamp,ROW_NUMBER() over (partition by isin order by timestamp desc) as rn from public.navhistory nh where nh.isin in (:isin) and nh.timestamp<=:date ) tem where tem.rn=1";

    private final String queryToGetCurrentNavForMf = "select isin,close,timestamp from ( select isin,close,timestamp,ROW_NUMBER() over (partition by isin order by timestamp desc) as rn from public.mfnavhistory nh where nh.isin in (:isin) and nh.timestamp<=:date ) tem where tem.rn=1";

    private final String queryToGetPreviousNavForStocks = "select isin,close,timestamp from ( select isin,close,timestamp,ROW_NUMBER() over (partition by isin order by timestamp asc) as rn from public.navhistory nh where nh.isin in ( :isin ) and timestamp>=:date ) tem where tem.rn=1";

    private final String queryToGetPreviousNavForMf = "select isin,close,timestamp from ( select isin,close,timestamp,ROW_NUMBER() over (partition by isin order by timestamp asc) as rn from public.mfnavhistory nh where nh.isin in ( :isin ) and timestamp>=:date ) tem where tem.rn=1";
    
    private final String queryToGetVolatileStocks = "select symbol,close,close-prevclose as change, ((close-prevclose)*100/prevclose) as perChange, \n" + 
            "timestamp as lastTradeDay from navhistory where timestamp=(select max(timestamp) from navhistory \n" + 
            "where timestamp>current_date-5) and series = 'EQ' and totaltrades>10000 order by perChange desc limit :size";
    
    private final String queryToGetVolatileStocksForPeriod = "select t.SYMBOL,t.close,(t.close-p.close) as change,\n" + 
            " TRUNC((t.close-p.close)*100/p.close,3) as perChange,t.timestamp as lastTradeDay,p.timestamp as refTradeDay\n" + 
            " from navhistory t inner join navhistory p \n" + 
            " on t.SYMBOL=p.SYMBOL and t.SERIES=p.SERIES where\n" + 
            " t.timestamp=(select max(timestamp) from navhistory \n" + 
            "where timestamp>current_date-5) \n" + 
            " and t.timestamp-p.timestamp=:dayCount \n" + 
            " and t.series = 'EQ' \n" + 
            " and t.totaltrades>10000\n" + 
            " order by perChange desc LIMIT :size";

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

    public Map<String, PriceDTO> getNavForStocks(LocalDate date, Set<String> isin) {
        Map<String, PriceDTO> ratesMap = new HashMap<String, PriceDTO>();

        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("date", date).addValue("isin", isin);

        List<PriceDTO> rates = namedParameterJdbcTemplate.query(queryToGetCurrentNavForStocks, namedParameters,
                new RowMapper<PriceDTO>() {

                    @Override
                    public PriceDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                        PriceDTO price = new PriceDTO();
                        price.setIsin(rs.getString("isin"));
                        price.setRate(rs.getBigDecimal("close"));
                        price.setNavDt(rs.getObject("timestamp", LocalDate.class));
                        return price;
                    }

                });

        if (!CollectionUtils.isEmpty(rates)) {
            rates.stream().forEach(pdto -> {
                ratesMap.put(pdto.getIsin(), pdto);
            });
        }
        return ratesMap;
    }

    public Map<String, PriceDTO> getPreviousNavForStocks(LocalDate date, Set<String> isin) {
        Map<String, PriceDTO> ratesMap = new HashMap<String, PriceDTO>();

        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("date", date).addValue("isin", isin);

        List<PriceDTO> rates = namedParameterJdbcTemplate.query(queryToGetPreviousNavForStocks, namedParameters,
                new RowMapper<PriceDTO>() {

                    @Override
                    public PriceDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                        PriceDTO price = new PriceDTO();
                        price.setIsin(rs.getString("isin"));
                        price.setRate(rs.getBigDecimal("close"));
                        price.setNavDt(rs.getObject("timestamp", LocalDate.class));
                        return price;
                    }

                });

        if (!CollectionUtils.isEmpty(rates)) {
            rates.stream().forEach(pdto -> {
                ratesMap.put(pdto.getIsin(), pdto);
            });
        }
        return ratesMap;
    }

    public Map<String, PriceDTO> getNavForMF(LocalDate date, Set<String> isin) {
        Map<String, PriceDTO> ratesMap = new HashMap<String, PriceDTO>();

        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("date", date).addValue("isin", isin);

        List<PriceDTO> rates = namedParameterJdbcTemplate.query(queryToGetCurrentNavForMf, namedParameters,
                new RowMapper<PriceDTO>() {

                    @Override
                    public PriceDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                        PriceDTO price = new PriceDTO();
                        price.setIsin(rs.getString("isin"));
                        price.setRate(rs.getBigDecimal("close"));
                        price.setNavDt(rs.getObject("timestamp", LocalDate.class));
                        return price;
                    }

                });

        if (!CollectionUtils.isEmpty(rates)) {
            rates.stream().forEach(pdto -> {
                ratesMap.put(pdto.getIsin(), pdto);
            });
        }
        return ratesMap;
    }

    public Map<String, PriceDTO> getPreviousNavForMF(LocalDate date, Set<String> isin) {
        Map<String, PriceDTO> ratesMap = new HashMap<String, PriceDTO>();

        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("date", date).addValue("isin", isin);

        List<PriceDTO> rates = namedParameterJdbcTemplate.query(queryToGetPreviousNavForMf, namedParameters,
                new RowMapper<PriceDTO>() {

                    @Override
                    public PriceDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                        PriceDTO price = new PriceDTO();
                        price.setIsin(rs.getString("isin"));
                        price.setRate(rs.getBigDecimal("close"));
                        price.setNavDt(rs.getObject("timestamp", LocalDate.class));
                        return price;
                    }

                });

        if (!CollectionUtils.isEmpty(rates)) {
            rates.stream().forEach(pdto -> {
                ratesMap.put(pdto.getIsin(), pdto);
            });
        }
        return ratesMap;
    }

    public List<CurrentHoldingsDTO> getCurrentHoldings(String assetType, String portfolioName) {
        
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("assetType", assetType).addValue("portfolioName", portfolioName);
        
        return namedParameterJdbcTemplate.query(queryGetHoldingsByAssetTypeAndPortfolio, namedParameters,new RowMapper<CurrentHoldingsDTO>() {

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

    public LocalDate getLastNavDate() {
        // TODO Auto-generated method stub
        return null;
    }

    public List<ScripPriceVO> getVolatileStocks() {
       SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("size", 100);
        
        return namedParameterJdbcTemplate.query(queryToGetVolatileStocks, namedParameters,new RowMapper<ScripPriceVO>() {

            @Override
            public ScripPriceVO mapRow(ResultSet rs, int rowNum) throws SQLException {
                ScripPriceVO dto = new ScripPriceVO();
                dto.setSchemeName(rs.getString("symbol"));
                dto.setLastNav(rs.getBigDecimal("close"));
                dto.setNavChange(rs.getBigDecimal("change"));
                dto.setNavChangePercent(rs.getBigDecimal("perChange"));
                dto.setLastNavDt(rs.getObject("lastTradeDay", LocalDate.class));   
                return dto;
            }

        });
        
    }
    
    public List<ScripPriceVO> getVolatileStocksForPeriod(int dayCount) {
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("dayCount", dayCount).addValue("size", 100);
         
         return namedParameterJdbcTemplate.query(queryToGetVolatileStocksForPeriod, namedParameters,new RowMapper<ScripPriceVO>() {

             @Override
             public ScripPriceVO mapRow(ResultSet rs, int rowNum) throws SQLException {
                 ScripPriceVO dto = new ScripPriceVO();
                 dto.setSchemeName(rs.getString("symbol"));
                 dto.setLastNav(rs.getBigDecimal("close"));
                 dto.setNavChange(rs.getBigDecimal("change"));
                 dto.setNavChangePercent(rs.getBigDecimal("perChange"));
                 dto.setLastNavDt(rs.getObject("lastTradeDay", LocalDate.class));
                 dto.setRefNavDt(rs.getObject("refTradeDay", LocalDate.class));   
                 return dto;
             }

         });
         
     }

}
