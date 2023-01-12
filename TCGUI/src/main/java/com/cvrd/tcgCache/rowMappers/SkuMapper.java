package com.cvrd.tcgCache.rowMappers;

import com.cvrd.tcgCache.records.Sku;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SkuMapper  implements RowMapper<Sku> {

    @Override
    public Sku mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Sku(
                rs.getInt("skuId"),
                rs.getInt("productId"),
                rs.getInt("languageId"),
                rs.getInt("printingId"),
                rs.getInt("conditionId"),
                rs.getInt("count")
        );
    }
}