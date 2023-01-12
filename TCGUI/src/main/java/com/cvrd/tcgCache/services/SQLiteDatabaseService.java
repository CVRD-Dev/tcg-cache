package com.cvrd.tcgCache.services;

import com.cvrd.tcgCache.records.*;
import com.cvrd.tcgCache.spi.DatabaseService;
import com.cvrd.tcgCache.spi.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

@Repository
public class SQLiteDatabaseService implements DatabaseService {

    @Value( "${spring.datasource.url}" )
    private String DB_URL;

    @Autowired
    TableService<Category> categoryService;
    @Autowired
    TableService<Condition> conditionService;
    @Autowired
    TableService<Group> groupService;
    @Autowired
    TableService<Language> languageService;
    @Autowired
    TableService<Print> printService;
    @Autowired
    TableService<Product> productService;
    @Autowired
    TableService<Sku> skuService;
    @Autowired
    TableService<SkuPrice> skuPriceService;

    @Autowired
    TCGPlayerClient tcgPlayerClient;

    @Override
    public void createDb() {

    }

    @Override
    public boolean doesDBExist() {
        boolean exist = false;
        System.out.println(DB_URL);
        try {
            Connection conn = DriverManager.getConnection(DB_URL);
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                exist = true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return exist;
    }

    @Override
    public TableService<Category> categoryService() {
        return this.categoryService;
    }

    @Override
    public TableService<Condition> conditionService() {
        return this.conditionService;
    }

    @Override
    public TableService<Group> groupService() {
        return this.groupService;
    }

    @Override
    public TableService<Language> languageService() {
        return this.languageService;
    }

    @Override
    public TableService<Print> printService() {
        return this.printService;
    }

    @Override
    public TableService<Product> productService() {
        return this.productService;
    }

    @Override
    public TableService<Sku> skuService() {
        return this.skuService;
    }

    @Override
    public TableService<SkuPrice> skuPriceService() {
        return this.skuPriceService;
    }

    @Override
    public TCGPlayerClient tcgPlayerClient() {
        return this.tcgPlayerClient;
    }
    
    @Override
    public boolean areTablesEmpty() {
        boolean empty = true;

        if(!(conditionService.isTableEmpty() && groupService.isTableEmpty() &&
            languageService.isTableEmpty() && printService.isTableEmpty() && productService.isTableEmpty() &&
            skuService.isTableEmpty() && skuPriceService.isTableEmpty())) {
            empty = false;
        }
        return empty;
    }
}
