package com.cvrd.tcgCache.rowMappers;

import com.cvrd.tcgCache.records.Language;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LanguageMapper  implements RowMapper<Language> {

    @Override
    public Language mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Language(
                rs.getInt("languageId"),
                rs.getString("name"),
                rs.getString("abbr")
        );
    }
}