package com.cvrd.tcgCache.TCGUI.views;

import com.cvrd.tcgCache.records.Product;
import com.cvrd.tcgCache.records.Sku;
import com.cvrd.tcgCache.spi.DatabaseService;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.NativeButtonRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Route(value = "products/:groupId")
public class ProductView extends VerticalLayout implements BeforeEnterObserver  {

    private int groupId = 0;

    @Autowired
    DatabaseService dbService;

    public ProductView(DatabaseService dbService) {
        this.dbService = dbService;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        this.groupId = Integer.parseInt(event.getRouteParameters().get("groupId").get());

        List<Product> products = dbService.productService().conditionalGet(String.format("WHERE groupId=%s", groupId));
        List<Integer> productIds = new ArrayList<>();
        for(Product product: products) {
            productIds.add(product.productId());
        }



        Grid<Product> grid = new Grid<>();
        grid.setItems(products);
        grid.addComponentColumn(product -> new Image(product.imageUrl(), product.cleanName()));
        grid.addComponentColumn(product -> {
            Grid<Sku> skuGrid = new Grid<>();
            List<Sku> skus = this.dbService.skuService().conditionalGet(String.format("WHERE productId=%s",product.productId()));
            skuGrid.setItems(skus);
            skuGrid.addComponentColumn(sku -> {
                String printName = this.dbService.printService().conditionalGet(String.format("WHERE printingId=%s", sku.printingId())).get(0).name();
                return new Span(printName);
            }).setHeader("print");
            skuGrid.addComponentColumn(sku -> {
               String condition = this.dbService.conditionService().conditionalGet(String.format("WHERE conditionId=%s",sku.conditionId())).get(0).abbreviation();
               return new Span(condition);
            }).setHeader("condition");
            skuGrid.addComponentColumn(sku -> {
               String language = this.dbService.languageService().conditionalGet(String.format("WHERE languageId=%s", sku.languageId())).get(0).abbr();
               return new Span(language);
            }).setHeader("language");
            skuGrid.addComponentColumn(sku -> {
                BigDecimal market = this.dbService.skuPriceService().conditionalGet(String.format("WHERE skuId=%s", sku.skuId())).get(0).marketPrice();
                return new Span(market.toString());
            }).setHeader("market price");
            skuGrid.addComponentColumn(sku -> {
                BigDecimal low = this.dbService.skuPriceService().conditionalGet(String.format("WHERE skuId=%s", sku.skuId())).get(0).lowPrice();
                return new Span(low.toString());
            }).setHeader("low price");
            skuGrid.addComponentColumn(sku -> {
                IntegerField field = new IntegerField ();
               field.setValue(sku.count());
                field.addValueChangeListener(valueChangeEvent -> {
                    try {
                        this.dbService.skuService().updateItem(sku.withCount(valueChangeEvent.getValue()),"skuId", Sku.class.getRecordComponents());
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });
                return field;
            });
            return skuGrid;
        });
        grid.setHeight("100%");
        setHeight("100%");
        add(grid);
//        grid.addColumn(new Image(Product::imageUrl));
//
//
//
//
//        VirtualList<Product> productVirtualList = new VirtualList<>();
//        productVirtualList.setItems(products);
//        productVirtualList.setRenderer(new Image(
//          item ->
//        );
//
//                new NativeButtonRenderer<Product>(
//                item -> item.name(),
//                clickedItem -> {
//                    productVirtualList.getUI().ifPresent(ui -> {
//                        ui.navigate(String.format("item/%s", clickedItem.productId()));
//                    });
//                }
//        ));
//    }
//
//
//    private static Renderer<Product> productImageRenderer() {
//        return LitRenderer.<Product> of(
//                <
//        )
    }

}
