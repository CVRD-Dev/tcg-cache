package com.cvrd.tcgCache.TCGUI.views;

import com.cvrd.tcgCache.records.*;
import com.cvrd.tcgCache.spi.DatabaseService;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@Route(value = "categorySelect")
public class CategorySelectorView extends AppLayout implements BeforeEnterObserver {



    @Autowired
    DatabaseService dbService;


    public CategorySelectorView(DatabaseService db) throws IOException {
        this.dbService = db;
        List<Category> categories = new ArrayList<>(); //dbService.categoryService().getAll();
        if(categories.size() == 0) {
            try {
                categories = dbService.tcgPlayerClient().getCategories();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        CheckboxGroup<Category> categoryGroup = new CheckboxGroup<>();
        categoryGroup.setItemLabelGenerator(category -> category.displayName());
        categoryGroup.setItems(categories);
        categoryGroup.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);

        Button button = new Button("Select");
        List<Category> finalCategories = categories;
        button.addClickListener(clickEvent -> {
            List<Category> trackedCategories = categoryGroup.getValue().stream().toList();
            //set tracked to true for all categories returned
            List<Category> addedCategories = new ArrayList<>();
            //merged tracked and untracked category lists
            for (Category finalCategory : finalCategories) {
                if (trackedCategories.contains(finalCategory)) {
                    addedCategories.add(finalCategory.setCollecting(1));
                } else {
                    addedCategories.add(finalCategory);
                }
            }
            try {
                dbService.categoryService().addItems(addedCategories, Category.class.getRecordComponents());
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            //route back to home screen
            button.getUI().ifPresent(ui -> ui.navigate("home"));
        });
        Div content = new Div();
        content.add(
                new H1("select categories to track:"),
                button,
                categoryGroup
        );
        setContent(content);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        System.out.println("CHASE");
        if (dbService.doesDBExist()) {
            System.out.println("creating database and tables");
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
