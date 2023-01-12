package com.cvrd.tcgCache.services;

import com.cvrd.tcgCache.TCGUI.views.DownloadingDataView;
import com.cvrd.tcgCache.TCGUI.views.LoadingView;
import com.cvrd.tcgCache.spi.TableService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.progressbar.ProgressBar;
import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.validation.constraints.Null;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;


public class SQLiteTableService<T> implements TableService<T> {

    protected String tableName = "NONE";
    protected String createTable = "CREATE TABLE IF NOT EXISTS %s";
    protected List<Triplet<String,String,String>> columns = null;

    protected UI ui;
    protected DownloadingDataView view;

    @Value( "${spring.datasource.url}" )
    private String dbUrl;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RowMapper<T> rowMapper;

    private final String idColumn;

    @Autowired
    private ReentrantLock mutex;

    public SQLiteTableService(String tableName, List<Triplet<String,String,String>> columns, String idColumn, RowMapper<T> rowMapper) {
        this.tableName = tableName;
        this.columns = columns;
        this.idColumn = idColumn;
        this.rowMapper = rowMapper;
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
    public void setUIComp(UI ui, DownloadingDataView view) {
        this.ui = ui;
        this.view = view;
    }

    @Override
    public synchronized void addItems(List<T> items, RecordComponent[] recordComponents) throws InvocationTargetException, IllegalAccessException {
        System.out.println(String.format("Beginning to download %s items to table: %s", items.size(), tableName));
        StringBuilder sql = new StringBuilder("BEGIN TRANSACTION;\n");
        for (T item : items) {
            StringBuilder insertStmt = new StringBuilder(String.format("INSERT INTO %s VALUES(",tableName));
            //TODO add to the sql statement based on the column type
            for (int i=0;i<recordComponents.length;i++) {
                insertStmt.append("\"");
                try{
                    String insrt = recordComponents[i].getAccessor().invoke(item).toString().replace("\"","'");
                    insertStmt.append(insrt);
                }
                catch (NullPointerException e) {
                    insertStmt.append(0.00);
                }
                insertStmt.append("\"");
                if (i+1 != recordComponents.length) {
                    insertStmt.append(",");
                }
            }
            insertStmt.append(");\n");
            sql.append(insertStmt);
        }
        sql.append("END TRANSACTION;");
//        System.out.println(sql.toString());
        System.out.println(mutex.getQueueLength());
        mutex.lock();
        System.out.println("lock held by " + items.toString());
        int updatedRows = jdbcTemplate.update(sql.toString());
        System.out.println(String.format("Finished downloading for table: %s", tableName));
        System.out.println("updated rows: " + updatedRows);
        mutex.unlock();
    }

    @Override
    public void updateItem(T item, String idColumn,RecordComponent[] recordComponents) throws InvocationTargetException, IllegalAccessException {
        StringBuilder sql = new StringBuilder(String.format("UPDATE %S",tableName));
        int id = 0;
        sql.append(" SET ");
        for (int i=0; i<recordComponents.length; i++) {
            sql.append(recordComponents[i].getName());
            sql.append(" = ");
            sql.append(recordComponents[i].getAccessor().invoke(item).toString());
            if (i+1 != recordComponents.length) {
                sql.append(",");
            }
            if (recordComponents[i].getName().equals(idColumn)) {
                id = (int) recordComponents[i].getAccessor().invoke(item);
            }
        }
        sql.append(String.format(" WHERE %s = %s", idColumn, id));
        jdbcTemplate.update(sql.toString());
    }

    @Override
    public boolean isTableEmpty() {
        boolean empty = true;
        String query = String.format("SELECT COUNT(1) FROM %S", tableName);
        Integer count = jdbcTemplate.queryForObject(query, Integer.class);
        if(count > 0) {
            empty = false;
        }
        return empty;
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
}
