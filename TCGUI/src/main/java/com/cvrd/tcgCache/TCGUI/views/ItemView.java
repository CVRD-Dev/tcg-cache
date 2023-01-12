package com.cvrd.tcgCache.TCGUI.views;

import com.cvrd.tcgCache.records.Category;
import com.cvrd.tcgCache.records.Sku;
import com.cvrd.tcgCache.spi.DatabaseService;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.NativeButtonRenderer;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route("item/:productId")
public class ItemView extends AppLayout {

    @Autowired
    DatabaseService dbService;

    public ItemView(DatabaseService db) {
        this.dbService = db;

        List<Sku> categories = dbService.skuService().conditionalGet("WHERE collecting=1");


//        setContent();
    }
}