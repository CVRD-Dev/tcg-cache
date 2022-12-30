package com.cvrd.tcgCache.rowMappers;


import org.springframework.jdbc.core.RowMapper;
import com.cvrd.tcgCache.records.Category;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class CategoryMapper implements RowMapper<Category> {
    @Override
    public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Category(
                rs.getInt("categoryId"),
                rs.getString("name"),
                rs.getString("displayName"),
                rs.getInt("collecting")
        );
    }
}
