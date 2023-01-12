package com.cvrd.tcgCache.rowMappers;

import com.cvrd.tcgCache.records.Group;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GroupMapper  implements RowMapper<Group> {

    @Override
    public Group mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Group(
                rs.getInt("groupId"),
                rs.getString("name"),
                rs.getString("abbr"),
                rs.getString("publishedOn"),
                rs.getInt("categoryId")
        );
    }
}