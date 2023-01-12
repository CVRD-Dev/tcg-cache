package com.cvrd.tcgCache.TCGUI.views;

import com.cvrd.tcgCache.records.Category;
import com.cvrd.tcgCache.spi.DatabaseService;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.NativeButtonRenderer;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route(value = "categories")
public class CategoryView extends VerticalLayout {

    @Autowired
    DatabaseService dbService;

    public CategoryView(DatabaseService db) {
        this.dbService = db;

        List<Category> categories = dbService.categoryService().conditionalGet("WHERE collecting=1");

        VirtualList<Category> categoryVirtualList = new VirtualList<>();
        categoryVirtualList.setItems(categories);
        categoryVirtualList.setRenderer(new NativeButtonRenderer<Category>(
                item -> item.displayName(),
                clickedItem -> {
                    categoryVirtualList.getUI().ifPresent(ui -> {
                                ui.navigate(String.format("groups/%s", clickedItem.categoryId()));
                            });
                }));
        add(categoryVirtualList);
    }
}
