package com.cvrd.tcgCache.TCGUI.views;

import com.cvrd.tcgCache.records.Category;
import com.cvrd.tcgCache.records.Group;
import com.cvrd.tcgCache.services.TCGPlayerClient;
import com.cvrd.tcgCache.spi.DatabaseService;

import java.util.ArrayList;
import java.util.List;

public class DownloadingDataView AppLayout{

    DatabaseService dbService;
    TCGPlayerClient tcgPlayerClient;

    public DownloadingDataView(DatabaseService db, TCGPlayerClient client) {
        this.dbService = db;
        this.tcgPlayerClient = client;

        List<Category> categories = dbService.categoryService().conditionalGet("WHERE collecting=1");
        List<int> catIds = new ArrayList<>();
        for (Category category: categories) {
            catIds.add(category.categoryId());
        }
        List<Group> groups = client.getGroups(catIds.toArray());
    }
}
