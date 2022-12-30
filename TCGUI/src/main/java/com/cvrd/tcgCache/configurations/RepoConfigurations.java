package com.cvrd.tcgCache.configurations;

import com.cvrd.tcgCache.records.*;
import com.cvrd.tcgCache.rowMappers.CategoryMapper;
import com.cvrd.tcgCache.services.SQLiteTableService;
import com.cvrd.tcgCache.spi.TableService;
import org.javatuples.Triplet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RepoConfigurations {

    @Bean
    public TableService<Category> categoryService() {
        List<Triplet<String, String, String>> columns = new ArrayList<>();
        columns.add(new Triplet<>("categoryId", "integer", "PRIMARY KEY"));
        columns.add(new Triplet<>("name", "text", ""));
        columns.add(new Triplet<>("displayName", "text", ""));
        columns.add(new Triplet<>("collecting", "boolean", ""));
        return new SQLiteTableService<>("Categories",columns,"categoryId");
    }

    @Bean
    public TableService<Condition> conditionService() {
        List<Triplet<String, String, String>> columns = new ArrayList<>();
        columns.add(new Triplet<>("conditionId", "integer", "PRIMARY KEY"));
        columns.add(new Triplet<>("name", "text", ""));
        columns.add(new Triplet<>("abbreviation", "text", ""));
        columns.add(new Triplet<>("displayOrder", "integer", ""));
        return new SQLiteTableService<>("Conditions",columns,"conditionId");
    }

    @Bean
    public TableService<Group> groupService() {
        List<Triplet<String, String, String>> columns = new ArrayList<>();
        columns.add(new Triplet<>("groupId", "integer", "PRIMARY KEY"));
        columns.add(new Triplet<>("name", "text", ""));
        columns.add(new Triplet<>("abbr", "text", ""));
        columns.add(new Triplet<>("publishedOn", "text", ""));
        columns.add(new Triplet<>("categoryId", "integer", ""));
        columns.add(new Triplet<>("FOREIGN KEY (categoryId)", "REFERENCES Categories (categoryId)", ""));
        return new SQLiteTableService<>("Groups",columns,"groupId");
    }


    @Bean
    public TableService<Language> languageService() {
        List<Triplet<String, String, String>> columns = new ArrayList<>();
        columns.add(new Triplet<>("languageId", "integer", "PRIMARY KEY"));
        columns.add(new Triplet<>("name", "text", ""));
        columns.add(new Triplet<>("abbr", "text", ""));
        return new SQLiteTableService<>("Languages",columns, "languageId");
    }

    @Bean
    public TableService<Print> printService() {
        List<Triplet<String, String, String>> columns = new ArrayList<>();
        columns.add(new Triplet<>("printingId", "integer", "PRIMARY KEY"));
        columns.add(new Triplet<>("name", "text", ""));
        columns.add(new Triplet<>("displayOrder", "integer", ""));
        return new SQLiteTableService<>("Prints",columns, "printingId");
    }

    @Bean
    public TableService<Product> productService() {
        List<Triplet<String, String, String>> columns = new ArrayList<>();
        columns.add(new Triplet<>("productId", "integer", "PRIMARY KEY"));
        columns.add(new Triplet<>("name", "text", ""));
        columns.add(new Triplet<>("cleanName", "text", ""));
        columns.add(new Triplet<>("imageUrl", "text", ""));
        columns.add(new Triplet<>("categoryId", "integer", ""));
        columns.add(new Triplet<>("groupId", "integer", ""));
        columns.add(new Triplet<>("url", "text", ""));
        columns.add(new Triplet<>("FOREIGN KEY (categoryId)", "REFERENCES Categories (categoryId)", ""));
        columns.add(new Triplet<>("FOREIGN KEY (groupId)", "REFERENCES Groups (groupId)", ""));
        return new SQLiteTableService<>("Products",columns, "productId");
    }

    @Bean
    public TableService<Sku> skuService() {
        List<Triplet<String, String, String>> columns = new ArrayList<>();
        columns.add(new Triplet<>("skuId", "integer", "PRIMARY KEY"));
        columns.add(new Triplet<>("productId", "integer", ""));
        columns.add(new Triplet<>("languageId", "integer", ""));
        columns.add(new Triplet<>("printingId", "integer", ""));
        columns.add(new Triplet<>("conditionId", "integer", ""));
        columns.add(new Triplet<>("count", "integer", ""));
        columns.add(new Triplet<>("FOREIGN KEY (productId)", "REFERENCES Products (productId)", ""));
        columns.add(new Triplet<>("FOREIGN KEY (languageId)", "REFERENCES Languages (languageId)", ""));
        columns.add(new Triplet<>("FOREIGN KEY (printingId)", "REFERENCES Prints (printingId)", ""));
        columns.add(new Triplet<>("FOREIGN KEY (conditionId)", "REFERENCES Conditions (conditionId)", ""));
        return new SQLiteTableService<>("Skus",columns, "skuId");
    }

    @Bean
    public TableService<SkuPrice> skuPriceService() {
        List<Triplet<String, String, String>> columns = new ArrayList<>();
        columns.add(new Triplet<>("skuId", "integer", "PRIMARY KEY"));
        columns.add(new Triplet<>("lowPrice", "real", ""));
        columns.add(new Triplet<>("marketPrice", "real", ""));
        return new SQLiteTableService<>("SkuPrices",columns, "skuId");
    }
}

