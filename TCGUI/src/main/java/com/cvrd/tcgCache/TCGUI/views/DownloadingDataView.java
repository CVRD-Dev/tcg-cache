package com.cvrd.tcgCache.TCGUI.views;

import com.cvrd.tcgCache.services.TCGPlayerClient;
import com.cvrd.tcgCache.spi.DatabaseService;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.Route;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static java.lang.Thread.sleep;

@Route(value = "download")
public class DownloadingDataView extends AppLayout {

    DatabaseService dbService;
    TCGPlayerClient tcgPlayerClient;

    ProgressBar progressBar = new ProgressBar();
    Div progressBarLabel = new Div();


    public DownloadingDataView(DatabaseService db, TCGPlayerClient client) throws IOException, InterruptedException {
        this.dbService = db;
        this.tcgPlayerClient = client;


//        progressBar.setIndeterminate(true);
        progressBar.setValue(0.1);
        progressBarLabel.setText("Generating report...");
        progressBarLabel.add(progressBar);

        setContent(progressBarLabel);
        double value = 0.1;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {

        DownloadThread thread = new DownloadThread(attachEvent.getUI(), this, this.progressBar, this.progressBarLabel);
        thread.start();
    }

    private static class DownloadThread extends Thread {
        private final UI ui;
        private final AppLayout layout;
        private final ProgressBar progressBar;
        private final Div label;

        private DownloadThread(UI ui, AppLayout layout, ProgressBar progressBar, Div label) {
            this.ui = ui;
            this.layout = layout;
            this.progressBar = progressBar;
            this.label = label;
        }

        @Async
        @Override
        public void run() {
            ui.access(() -> {
                double count = 0.0;
                while (count < 10.0) {
                    System.out.println("counting now: " + String.valueOf(count));
                    progressBar.setValue(count);
                    label.setText("Currently at: " + String.valueOf(count));
                    count+=0.1;
                    try {
                        sleep(10000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                }
            });
        }
        }
    }

//        List<Category> categories = dbService.categoryService().conditionalGet("WHERE collecting=1");
//        List<Integer> catIds = new ArrayList<>();
//        for (Category category: categories) {
//            catIds.add(category.categoryId());
//        }
//        List<Group> groups = client.getGroups(catIds);
//        List<Condition> conditions = client.getConditions(catIds);
//        List<Print> prints = client.getPrints(catIds);
//
//        List<Integer> groupIds = new ArrayList<>();
//        for (Group group: groups) {
//            groupIds.add(group.groupId());
//        }
//        List<Product> products = client.getCardProducts(groupIds);
//
//        List<Integer> productIds = new ArrayList<>();
//        for (Product product: products) {
//            productIds.add(product.productId());
//        }
//        List<Sku> skus = client.getSkus(productIds);

