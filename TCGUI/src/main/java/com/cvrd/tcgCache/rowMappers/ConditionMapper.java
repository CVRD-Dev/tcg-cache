package com.cvrd.tcgCache.rowMappers;

import com.cvrd.tcgCache.records.Condition;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ConditionMapper implements RowMapper<Condition> {

    @Override
    public Condition mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Condition(
                rs.getInt("conditionId"),
                rs.getString("name"),
                rs.getString("abbreviation"),
                rs.getInt("displayOrder")
        );
    }
}