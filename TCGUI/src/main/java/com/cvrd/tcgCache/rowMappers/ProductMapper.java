package com.cvrd.tcgCache.rowMappers;

import com.cvrd.tcgCache.records.Product;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductMapper  implements RowMapper<Product> {

    @Override
    public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Product(
                rs.getInt("productId"),
                rs.getString("name"),
                rs.getString("cleanName"),
                rs.getString("imageUrl"),
                rs.getInt("categoryId"),
                rs.getInt("groupId"),
                rs.getString("url")
        );
    }
}