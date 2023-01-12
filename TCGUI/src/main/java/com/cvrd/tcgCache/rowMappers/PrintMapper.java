package com.cvrd.tcgCache.rowMappers;

import com.cvrd.tcgCache.records.Print;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PrintMapper  implements RowMapper<Print> {

    @Override
    public Print mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Print(
                rs.getInt("printingId"),
                rs.getString("name"),
                rs.getInt("displayOrder")
        );
    }
}