package com.cvrd.tcgCache.TCGUI.views;

import com.cvrd.tcgCache.records.*;
import com.cvrd.tcgCache.services.TCGPlayerClient;
import com.cvrd.tcgCache.spi.DatabaseService;
import com.vaadin.flow.component.applayout.AppLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DownloadingDataView extends AppLayout {

    DatabaseService dbService;
    TCGPlayerClient tcgPlayerClient;

    public DownloadingDataView(DatabaseService db, TCGPlayerClient client) throws IOException {
        this.dbService = db;
        this.tcgPlayerClient = client;

        List<Category> categories = dbService.categoryService().conditionalGet("WHERE collecting=1");
        List<Integer> catIds = new ArrayList<>();
        for (Category category: categories) {
            catIds.add(category.categoryId());
        }
        List<Group> groups = client.getGroups(catIds);
        List<Condition> conditions = client.getConditions(catIds);
        List<Print> prints = client.getPrints(catIds);

        List<Integer> groupIds = new ArrayList<>();
        for (Group group: groups) {
            groupIds.add(group.groupId());
        }
        List<Product> products = client.getCardProducts(groupIds);

        List<Integer> productIds = new ArrayList<>();
        for (Product product: products) {
            productIds.add(product.productId());
        }
        List<Sku> skus = client.getSkus(productIds);


    }
}
