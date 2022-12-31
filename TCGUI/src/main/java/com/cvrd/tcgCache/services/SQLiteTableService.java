package com.cvrd.tcgCache.services;

import com.cvrd.tcgCache.TCGUI.views.LoadingView;
import com.cvrd.tcgCache.rowMappers.CategoryMapper;
import com.cvrd.tcgCache.spi.TableService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.progressbar.ProgressBar;
import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;


public class SQLiteTableService<T> extends Thread implements TableService<T> {

    protected String tableName = "NONE";
    protected String createTable = "CREATE TABLE IF NOT EXISTS %s";
    protected List<Triplet<String,String,String>> columns = null;

    @Value( "${spring.datasource.url}" )
    private String dbUrl;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RowMapper<T> rowMapper;

    private final String idColumn;

    //used to update UI progress bar
    private UI ui;
    private LoadingView view;

    public SQLiteTableService(String tableName, List<Triplet<String,String,String>> columns, String idColumn) {
        this.tableName = tableName;
        this.columns = columns;
        this.idColumn = idColumn;
    }


    @Override
    public boolean tableExist() {
        boolean exist = true;
        String query =
                "select count(*) "
                        + "from information_schema.tables "
                        + "where table_name = ? and table_schema = 'dbo'";
        Integer result = jdbcTemplate.queryForObject(query, Integer.class, tableName);
        if (result == 0) {
            exist = false;
        }
        return exist;
    }

    @Override
    public void createTable() {
        try{
            Connection conn = DriverManager.getConnection(dbUrl);
            Statement stmt = conn.createStatement();
            String sql = generateCreateTableStatement();
            System.out.println(sql);
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(String.format("ERROR Creating table: %s", tableName));
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<T> getAll() {
        String sql = String.format("SELECT * from %s", tableName);
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public T get(int id) {
        String sql = String.format("SELECT * from %s WHERE %s = %s", tableName, idColumn, id);
        return jdbcTemplate.queryForObject(sql,rowMapper);
    }

    @Override
    public List<T> conditionalGet(String conditionalSql) {
        StringBuilder sql = new StringBuilder(String.format("SELECT * FROM %s", tableName));
        sql.append(" ").append(conditionalSql);
        return jdbcTemplate.query(sql.toString(),rowMapper);
    }

    @Override
    public void addItems(List<T> items, RecordComponent[] recordComponents) throws InvocationTargetException, IllegalAccessException {
        StringBuilder sql = new StringBuilder("BEGIN TRANSACTION;\n");
        for (T item : items) {
            StringBuilder insertStmt = new StringBuilder(String.format("INSERT INTO %s VALUES(",tableName));
            //TODO add to the sql statement based on the column type
            for (int i=0;i<recordComponents.length;i++) {
                insertStmt.append("'");
                insertStmt.append(recordComponents[i].getAccessor().invoke(item).toString());
                insertStmt.append("'");
                if (i+1 != recordComponents.length) {
                    insertStmt.append(",");
                }
            }
            insertStmt.append(");\n");
            sql.append(insertStmt);
        }
        sql.append("END TRANSACTION;");
        System.out.println(sql.toString());
        int updatedRows = jdbcTemplate.update(sql.toString());
        System.out.println(updatedRows);
    }

    @Override
    public void updateItem(T item) {

    }



    private String generateCreateTableStatement() {
        String sql = String.format(createTable, tableName);
        sql = sql + " (";

        for(int i=0; i < columns.size(); i++) {
            sql = sql + " " + columns.get(i).getValue0() + " " +
                    columns.get(i).getValue1() + " ";
            if (i < columns.size() -1) {
                sql = sql + columns.get(i).getValue2() + ", ";
            }
        }
        sql = sql + ");";
        return sql;
    }

    @Override
    public void run() {

    }
}
