package com.skumar.assetz.repo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.skumar.assetz.dto.CurrentHoldingsDTO;

@Repository
public class AssetsGenericRepo {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    String query = "select portfolio_name,asset_type,isin,scrip_name,avg_rate,quantity,invested_amt \n" + 
            "from current_holding order by portfolio_name,asset_type,isin";
    
    public List<CurrentHoldingsDTO> getCurrentHoldings(){
        
        return jdbcTemplate.query(query, new RowMapper<CurrentHoldingsDTO>() {

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
    

}
