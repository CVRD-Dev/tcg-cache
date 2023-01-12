package com.cvrd.tcgCache.rowMappers;

import com.cvrd.tcgCache.records.SkuPrice;
import org.springframework.stereotype.Service;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class SkuPriceMapper implements RowMapper<SkuPrice> {

    @Override
    public SkuPrice mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new SkuPrice(
                rs.getInt("skuId"),
                rs.getBigDecimal("lowPrice"),
                rs.getBigDecimal("marketPrice")

        );
    }
}
