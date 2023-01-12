package com.cvrd.tcgCache.TCGUI.views;

import com.cvrd.tcgCache.records.Sku;
import com.cvrd.tcgCache.records.SkuPrice;
import com.cvrd.tcgCache.services.TCGPlayerClient;
import com.cvrd.tcgCache.spi.DatabaseService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Route("temp")
public class tempview extends DownloadingDataView {

    @Autowired
    DatabaseService databaseService;
    @Autowired
    TCGPlayerClient client;
    public tempview(DatabaseService databaseService, TCGPlayerClient client) throws ExecutionException, InterruptedException, JsonProcessingException, InvocationTargetException, IllegalAccessException {
        super(databaseService,client);
        this.databaseService = databaseService;
        this.client = client;

        List<Sku> skus = databaseService.skuService().getAll();
        List<Integer> skuIds = new ArrayList<>();
        for (Sku sku: skus) {
            skuIds.add(sku.skuId());
        }
        client.setUIVars(UI.getCurrent(),this);

        Runnable task = () -> {
            List<SkuPrice> prices = null;
            try {
                prices = client.getSkuPrices(skuIds);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            try {
                databaseService.skuPriceService().addItems(prices, SkuPrice.class.getRecordComponents());
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        };
        Thread thread = new Thread(task);
        thread.start();

    }

    @Override
    protected void onAttach(AttachEvent event) {
        System.out.println("nope");
    }
}
