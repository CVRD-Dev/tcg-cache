package com.cvrd.tcgCache.TCGUI.views;

import com.cvrd.tcgCache.constants.DBItems;
import com.cvrd.tcgCache.records.*;
import com.cvrd.tcgCache.services.TCGPlayerClient;
import com.cvrd.tcgCache.spi.DatabaseService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static java.lang.Thread.sleep;

@Route(value = "download")
public class DownloadingDataView extends VerticalLayout implements BeforeEnterObserver {
    private DatabaseService dbService;
    private TCGPlayerClient client;
    public DownloadingDataView(DatabaseService dbService, TCGPlayerClient client) {
        this.dbService = dbService;
        this.client = client;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        this.client.setUIVars(attachEvent.getUI(), this);
        Thread thread = new Thread(buildDownloadRunnable());
        thread.start();
    }

    private Runnable buildDownloadRunnable() {
        Runnable runnable = () -> {
            List<Category> categories = dbService.categoryService().getAll();
            List<Integer> catIds = new ArrayList<>();
            for (Category category: categories) {
                if(category.tracking() == 1) {
                    catIds.add(category.categoryId());
                }
            }
            Runnable groupCallable = () -> {
                try {
                    List<Group> groups = this.client.getGroups(catIds);
                    //TODO: download to db
                    this.dbService.groupService().addItems(groups, Group.class.getRecordComponents());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            };

            ExecutorService service = Executors.newFixedThreadPool(10);
            service.submit(groupCallable);

            //CONDITIONS
            Runnable conditionRunnable = () -> {
                try {
                    List<Condition> conditions = this.client.getConditions(catIds);
                    //TODO: download conditions to db
                    this.dbService.conditionService().addItems(conditions, Condition.class.getRecordComponents());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            };

            Runnable printsRunnable = () -> {
                try {
                    List<Print> prints = this.client.getPrints(catIds);
                    //TODO: download prints to db
                    this.dbService.printService().addItems(prints,Print.class.getRecordComponents());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            };

            Runnable languageRunnable = () -> {
                try {
                    List<Language> languages = this.client.getLanguages(catIds);
                    this.dbService.languageService().addItems(languages, Language.class.getRecordComponents());
                    //TODO: download prints to db
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            };
            service.submit(conditionRunnable);
            service.submit(printsRunnable);
            service.submit(languageRunnable);

            Callable<List<Product>> productCallable = () -> {
                List<Product> products = this.client.getCardProducts(catIds);
                Runnable dbRunnable = () -> {
                    try {
                        this.dbService.productService().addItems(products, Product.class.getRecordComponents());
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                };
                service.submit(dbRunnable);
                return products;
            };
            Future<List<Product>> futureProducts = service.submit(productCallable);

            //TODO: download products to db
            List<Integer> productIds = new ArrayList<>();
            try {
                List<Product> finalProducts = futureProducts.get();
                for (Product product: finalProducts) {
                    productIds.add(product.productId());
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
            Runnable skuRunnable = () -> {
                Callable<List<Sku>> skuCallable = () -> {
                    List<Sku> skus = this.client.getSkus(productIds);
                    Runnable dbSkus = () -> {
                        try {
                            this.dbService.skuService().addItems(skus, Sku.class.getRecordComponents());
                        } catch (InvocationTargetException e) {
                            throw new RuntimeException(e);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    };
                    service.submit(dbSkus);
                    return skus;
                };
                Future<List<Sku>> futureSkus = service.submit(skuCallable);
                //TODO: download sku to db
                List<Integer> skuIds = new ArrayList<>();
                try {
                    List<Sku> finalSkus = futureSkus.get();
                    for (Sku sku: finalSkus) {
                        skuIds.add(sku.skuId());
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }

                Callable skuPrices = () -> {
                    try {
                        List<SkuPrice> prices = this.client.getSkuPrices(skuIds);
                        this.dbService.skuPriceService().addItems(prices, SkuPrice.class.getRecordComponents());
                        //TODO: get sku prices and download to db
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                    return true;
                };
                Future<Boolean> done = service.submit(skuPrices);
                Button homeButton = new Button("Home");
                homeButton.addClickListener(e -> {
                   homeButton.getUI().ifPresent(ui -> {
                       ui.navigate("home");
                   });
                });

                add(new Span("All downloads finished Please return home"), homeButton);
            };
            service.submit(skuRunnable);



        };
        return runnable;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        boolean empty = dbService.areTablesEmpty();
        if(!empty) {
            System.out.println("Tables are not empty. do not attempt to redownload");
            beforeEnterEvent.getUI().navigate("home");
        }
    }

}