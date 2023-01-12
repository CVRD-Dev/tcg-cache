package com.cvrd.tcgCache.services;

import com.cvrd.tcgCache.TCGUI.views.DownloadingDataView;
import com.cvrd.tcgCache.TCGUI.views.LoadingView;
import com.cvrd.tcgCache.constants.DBItems;
import com.cvrd.tcgCache.records.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.*;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.progressbar.ProgressBar;
import okio.BufferedSink;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.primes.Primes;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.sqlite.core.DB;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class TCGPlayerClient {

    @Value("${tcgplayer.token}")
    private String bearerToken;

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);;

    //URLs
    private static final String BASE_URL = "https://api.tcgplayer.com%s";
    private static final String CATEGORY_URL = "/catalog/categories?sortOrder=categoryId&offset=%s&limit=100";
    private static final String GROUP_URL = "/catalog/categories/%s/groups?offset=%s&limit=100";
    private static final String PRODUCT_URL = "/catalog/products?categoryId=%s&productTypes=Cards&offset=%s&limit=100";
    private static final String SKU_URL = "/catalog/products/%S/skus";
    private static final String CONDITION_URL = "/catalog/categories/%s/conditions";
    private static final String PRINTING_URL = "/catalog/categories/%s/printings";
    private static final String LANGUAGE_URL = "/catalog/categories/%s/languages";
    private static final String SKU_PRICE_URL = "/pricing/sku/%s";
    //HEADERS
    private static final String ACCEPT = "Accept";
    private static final String ACCEPT_VALUE = "application/json";
    private static final String AUTHORIZATION = "Authorization";
    private static final String AUTHORIZATION_VALUE = "bearer %s";

    private static final String TOTAL_ITEMS = "totalItems";

    private static final String LOADING_TEXT = "Loading %s items for %s: %s/%s";
    private static final String COMPLETE = "Finished loading %s";
    //used to update UI progress bar
    private UI ui;
    private DownloadingDataView view;

    //TODO: add logic to get the language and skuprice

    public TCGPlayerClient() {
        client.setConnectTimeout(10, TimeUnit.MINUTES);
        client.setReadTimeout(10, TimeUnit.MINUTES);
    }

    public List<Category> getCategories() throws JSONException, IOException {
        List<String> items = pagedSearch(CATEGORY_URL, DBItems.CATEGORIES);
        List<Category> returnList = new ArrayList<>();
        for (String item : items) {
            returnList.addAll(mapper.readValue(item, new TypeReference<List<Category>>() {
            }));
        }
        return returnList.stream().distinct().collect(Collectors.toList());
    }

    public List<Condition> getConditions(List<Integer> categoryIds) throws JSONException, IOException {
        List<Condition> returnList = new ArrayList<>();
        List<String> items = new ArrayList<>();
        ProgressBar progressBar = new ProgressBar();
        progressBar.setMax(categoryIds.size());
        Span desc = new Span("loading skus");
        Div progressDiv = new Div(desc, progressBar);
        ui.access(() -> {
            view.add(progressDiv);
        });
        double total = categoryIds.size();

        for (int i=0; i<categoryIds.size(); i++) {
            items.add(singleSearch(String.format(CONDITION_URL, categoryIds.get(i), "%s")));
            int finalI = i;
            ui.access(() -> {
                desc.setText(String.format(LOADING_TEXT, "conditions", "categories", finalI,  categoryIds.size()));
                progressBar.setValue(finalI);
            });
        }

        for (String item : items) {
            returnList.addAll(mapper.readValue(item, new TypeReference<List<Condition>>() {
            }));
        }
        ui.access(() -> {
            progressBar.setValue(categoryIds.size());
            desc.setText(String.format(COMPLETE, "conditions"));
        });
        return returnList.stream().distinct().collect(Collectors.toList());
    }

    public List<Group> getGroups(List<Integer> categoryIds) throws JSONException, IOException {
        List<Group> returnList = new ArrayList<>();
        List<String> items = new ArrayList<>();
        boolean firstPage = true;
        for (Integer categoryId : categoryIds) {
            items.addAll(pagedSearch(String.format(GROUP_URL, categoryId, "%s"), DBItems.GROUP));
        }
        for (String item : items) {
            returnList.addAll(mapper.readValue(item, new TypeReference<List<Group>>() {
            }));
        }
        return returnList.stream().distinct().collect(Collectors.toList());
    }

    public List<Product> getCardProducts(List<Integer> categoryIds) throws JSONException, IOException, InterruptedException, ExecutionException {
        List<Product> returnList = new ArrayList<>();

        List<Callable<List<String>>> tasks = new ArrayList<>();
        for (int groupId : categoryIds) {
            Callable<List<String>> callable = () -> {
                return pagedSearch(String.format(PRODUCT_URL, groupId, "%s"), DBItems.PRODUCT);
            };
            tasks.add(callable);
        }
        ExecutorService executorService = Executors.newFixedThreadPool(categoryIds.size());
        List<Future<List<String>>> items = executorService.invokeAll(tasks);
        for (Future<List<String>> item : items) {
            List<String> products = item.get();
            for (String product : products) {
                returnList.addAll(mapper.readValue(product, new TypeReference<List<Product>>() {

                }));
            }
        }

        return returnList.stream().distinct().collect(Collectors.toList());
    }

    public List<Sku> getSkus(List<Integer> productIds) throws JSONException, IOException, InterruptedException, ExecutionException {

        List<Sku> returnList = new ArrayList<>();
        List<Callable<String>> tasks = new ArrayList<>();
        ProgressBar progressBar = new ProgressBar();
        progressBar.setMax(productIds.size());
        Span desc = new Span("loading skus");
        Div progressDiv = new Div(desc, progressBar);
        ui.access(() -> {
            view.add(progressDiv);
        });
        for (int i=0;i<productIds.size(); i++) {
            int finalI = i;
            Callable<String> callable = () -> {
                ui.access(() -> {
                    desc.setText(String.format(LOADING_TEXT, "skus", "products", finalI,  productIds.size()));
                    progressBar.setValue(finalI);
                });
                return singleSearch(String.format(SKU_URL, productIds.get(finalI)));
            };
            tasks.add(callable);
        }
        ExecutorService executorService = Executors.newFixedThreadPool(6);
        List<Future<String>> items = executorService.invokeAll(tasks);

        for (Future<String> item : items) {
            String sku = item.get();
            System.out.println("about to make skus");
            returnList.addAll(mapper.readValue(sku, new TypeReference<List<Sku>>() {}));
            System.out.println("done mapping skus");

        }
        ui.access(() -> {
            progressBar.setValue(productIds.size());
            desc.setText(String.format(COMPLETE, "skus"));
        });
        return returnList.stream().distinct().collect(Collectors.toList());
    }

    public List<SkuPrice> getSkuPrices(List<Integer> skuIds) throws InterruptedException, ExecutionException, JsonProcessingException {
        List<SkuPrice> returnList = new ArrayList<>();
        List<Callable<String>> tasks = new ArrayList<>();

        ProgressBar progressBar = new ProgressBar();
        progressBar.setMax(skuIds.size());
        Span desc = new Span("loading sku prices");
        Div progressDiv = new Div(desc, progressBar);
        ui.access(() -> {
            view.add(progressDiv);
        });


        //group sku prices by prime. if none of predefined primes work, then do individually
//        List<Integer> primes = Primes.primeFactors(skuIds.size());
        int increaseAmount = 1;

        if (skuIds.size() % 2 == 0) {
            increaseAmount = 2;
        }
        else if (skuIds.size() % 3 == 0) {
            increaseAmount = 3;
        }

//        for (Integer integer: primes) {
//            if (integer > increaseAmount) {
//                increaseAmount = integer;
//            }
//        }

        //TODO: finish the bove logic

        System.out.println(String.format("Increase amount: %s", increaseAmount));
        for (int i=0;i<skuIds.size(); i+=increaseAmount) {
            int finalI = i;
            String skuId = Integer.toString(i);
            if (increaseAmount > 1) {
                List<Integer> subSkuIds = skuIds.subList(i, i+ increaseAmount);
                skuId = StringUtils.join(subSkuIds, ",");
            }


            String finalSkuId = skuId;
            Callable<String> callable = () -> {
                ui.access(() -> {
                    desc.setText(String.format(LOADING_TEXT, "sku prices", "skus", finalI,  skuIds.size()) + "... this is going to take a while. blame TCGPlayer for poor Rest Endpoints. Just don't let your PC go to sleep while this is running!!!!");
                    progressBar.setValue(finalI);
                });
                return singleSearch(String.format(SKU_PRICE_URL, finalSkuId));
            };
            tasks.add(callable);
        }
        ExecutorService executorService = Executors.newFixedThreadPool(6);
        List<Future<String>> items = executorService.invokeAll(tasks);

        for (Future<String> item : items) {
            String skuPrice = item.get();
            returnList.addAll(mapper.readValue(skuPrice, new TypeReference<List<SkuPrice>>() {}));
        }
        ui.access(() -> {
            progressBar.setValue(skuIds.size());
            desc.setText(String.format(COMPLETE, "skus"));
        });
        return returnList.stream().distinct().collect(Collectors.toList());
    }

    public List<Print> getPrints(List<Integer> categoryIds) throws JSONException, IOException {
        List<Print> returnList = new ArrayList<>();
        List<String> items = new ArrayList<>();
        ProgressBar progressBar = new ProgressBar();
        progressBar.setMax(categoryIds.size());
        Span desc = new Span("loading prints");
        Div progressDiv = new Div(desc, progressBar);
        ui.access(() -> {
            view.add(progressDiv);
        });

        double total = categoryIds.size();
        for (int i=0; i< categoryIds.size(); i++) {
            int finalI = i;
            items.add(singleSearch(String.format(PRINTING_URL, categoryIds.get(finalI), "%s")));
            ui.access(() -> {
                desc.setText(String.format(LOADING_TEXT, "prints", "categories", finalI,  categoryIds.size()));
                progressBar.setValue(finalI);
            });
        }

        for (String item : items) {
            returnList.addAll(mapper.readValue(item, new TypeReference<List<Print>>() {
            }));
        }
        ui.access(() -> {
            progressBar.setValue(categoryIds.size());
           desc.setText(String.format(COMPLETE, "prints"));
        });
        return returnList.stream().distinct().collect(Collectors.toList());
    }

    public List<Language> getLanguages(List<Integer> categoryIds) throws JSONException, IOException {
        List<Language> returnList = new ArrayList<>();
        List<String> items = new ArrayList<>();
        ProgressBar progressBar = new ProgressBar();
        progressBar.setMax(categoryIds.size());
        Span desc = new Span("loading prints");
        Div progressDiv = new Div(desc, progressBar);
        ui.access(() -> {
            view.add(progressDiv);
        });

        double total = categoryIds.size();

        for (int i=0; i< categoryIds.size(); i++) {

            items.add(singleSearch(String.format(LANGUAGE_URL, categoryIds.get(i), "%s")));
            int finalI = i;
            ui.access(() -> {
                desc.setText(String.format(LOADING_TEXT, "languages", "categories", finalI,  categoryIds.size()));
                progressBar.setValue(finalI);
            });
        }

        for (String item : items) {
            returnList.addAll(mapper.readValue(item, new TypeReference<List<Language>>() {
            }));
        }

        ui.access(() -> {
            progressBar.setValue(categoryIds.size());
            desc.setText(String.format(COMPLETE, "languages"));
        });
        return returnList.stream().distinct().collect(Collectors.toList());
    }

    private String singleSearch(String urlExtension) throws IOException, JSONException {
        String url = String.format(BASE_URL, urlExtension);
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader(ACCEPT, ACCEPT_VALUE)
                .addHeader(AUTHORIZATION, String.format(AUTHORIZATION_VALUE, bearerToken))
                .build();

        Response response = client.newCall(request).execute();
        JSONObject body = new JSONObject(response.body().string());
        JSONArray results = body.getJSONArray("results");
        return results.toString();
    }


    private List<String> pagedSearch(String urlExtension, DBItems item) throws IOException, JSONException {
        ProgressBar progressBar = new ProgressBar();
        Span desc = new Span(String.format("loading %s from %s", 0, urlExtension));
        Div progressDiv = new Div(desc, progressBar);
        double totalItems = 0.0;
        ui.access(() -> {
           view.add(progressDiv);
        });
        int offset = 0;
        String url = String.format(BASE_URL, urlExtension);
        List<String> returnList = new ArrayList<>();
        boolean firstPage = true;
        while (true) {
            Request request = new Request.Builder()
                    .url(String.format(url, offset))
                    .get()
                    .addHeader(ACCEPT, ACCEPT_VALUE)
                    .addHeader(AUTHORIZATION, String.format(AUTHORIZATION_VALUE, bearerToken))
                    .build();
            Response response = client.newCall(request).execute();
            JSONObject body = new JSONObject(response.body().string());

            //set max progress bar
            if (firstPage) {
                if(body.has(TOTAL_ITEMS)) {
                    totalItems =  Double.parseDouble(body.get(TOTAL_ITEMS).toString());
                    double finalTotalItems = totalItems;
                    ui.access(() -> {
                        progressBar.setMax(finalTotalItems);
                    });
                }
                firstPage = false;
            }

            JSONArray results = body.getJSONArray("results");
            returnList.add(results.toString());
            int newOffest = offset + results.length();

            //udpate the progress bar
            int finalTotalItems1 = (int) totalItems;
            ui.access(() -> {
                progressBar.setValue((double) newOffest);
                desc.setText(String.format("Loading %s/%s %s", newOffest, finalTotalItems1, item.toString()));
            });

            if (newOffest > offset) {
                offset = newOffest;
            }
            else {
                ui.access(() -> {
                   desc.setText(String.format("FINISHED loading %s",item.toString()));
                });
                break;
            }
        }
        return returnList;
    }

    public void setUIVars(UI ui, DownloadingDataView view) {
        this.ui = ui;
        this.view = view;
    }
}
