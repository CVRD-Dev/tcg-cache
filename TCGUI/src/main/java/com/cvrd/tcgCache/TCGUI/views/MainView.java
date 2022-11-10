package com.cvrd.tcgCache.TCGUI.views;

import com.cvrd.tcgCache.records.*;
import com.cvrd.tcgCache.spi.DatabaseService;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

@Route(value = "myHome")
public class MainView extends VerticalLayout implements BeforeEnterObserver {



    @Autowired
    DatabaseService dbService;


    public MainView(DatabaseService db) throws IOException {
        this.dbService = db;
        VerticalLayout todosList = new VerticalLayout();
        List<Category> categories = dbService.categoryService().getAll();
        if(categories.size() == 0) {
            categories = dbService.tcgPlayerClient().getCategories();
//            dbService.categoryService().
        }
        for (int i=0; i<categories.size();i++) {
            todosList.add(new Checkbox(categories.get(i).displayName()));
        }
        todosList.setAlignItems(Alignment.CENTER);
        add(
            new H1("select categories to track:"),
            todosList
        );
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (dbService.doesDBExist()) {
            dbService.categoryService().createTable();
            dbService.conditionService().createTable();
            dbService.groupService().createTable();
            dbService.languageService().createTable();
            dbService.printService().createTable();
            dbService.productService().createTable();
            dbService.skuService().createTable();
            dbService.skuPriceService().createTable();
            dbService.categoryService().getAll();
        }

    }
}
