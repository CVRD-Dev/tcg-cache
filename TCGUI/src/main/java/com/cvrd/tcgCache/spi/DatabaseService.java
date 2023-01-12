package com.cvrd.tcgCache.spi;

import com.cvrd.tcgCache.records.*;
import com.cvrd.tcgCache.services.SQLiteTableService;
import com.cvrd.tcgCache.services.TCGPlayerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

public interface DatabaseService {
    void createDb();
    boolean doesDBExist();

    boolean areTablesEmpty();

    TableService<Category> categoryService();
    TableService<Condition> conditionService();
    TableService<Group> groupService();
    TableService<Language> languageService();
    TableService<Print> printService();
    TableService<Product> productService();
    TableService<Sku> skuService();
    TableService<SkuPrice> skuPriceService();

    TCGPlayerClient tcgPlayerClient();
}
