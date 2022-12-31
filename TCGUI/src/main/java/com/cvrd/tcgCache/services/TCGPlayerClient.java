package com.cvrd.tcgCache.services;

import com.cvrd.tcgCache.TCGUI.views.LoadingView;
import com.cvrd.tcgCache.records.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.*;
import com.vaadin.flow.component.UI;
import okio.BufferedSink;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    private static final String PRODUCT_URL = "/catalog/products?groupId=%s&productTypes=Cards&offset=%s&limit=100";
    private static final String SKU_URL = "/catalog/products/%S/skus";
    private static final String CONDITION_URL = "/catalog/categories/%s/conditions";
    private static final String PRINTING_URL = "/catalog/categories/%s/printings";

    //HEADERS
    private static final String ACCEPT = "Accept";
    private static final String ACCEPT_VALUE = "application/json";
    private static final String AUTHORIZATION = "Authorization";
    private static final String AUTHORIZATION_VALUE = "bearer %s";

    //used to update UI progress bar
    private UI ui;
    private LoadingView view;

    //TODO: add logic to get the language and skuprice

    public List<Category> getCategories() throws JSONException, IOException {
        List<String> items = pagedSearch(CATEGORY_URL);
        List<Category> returnList = new ArrayList<>();
        for (String item : items) {
            returnList.addAll(mapper.readValue(item, new TypeReference<List<Category>>() {
            }));
        }
        return returnList;
    }

    public List<Condition> getConditions(List<Integer> categoryIds) throws JSONException, IOException {
        List<Condition> returnList = new ArrayList<>();
        List<String> items = new ArrayList<>();
        for (int categoryId : categoryIds) {
            items.add(singleSearch(String.format(CONDITION_URL, categoryId, "%s")));
        }

        for (String item : items) {
            returnList.addAll(mapper.readValue(item, new TypeReference<List<Condition>>() {
            }));
        }
        return returnList.stream().distinct().collect(Collectors.toList());
    }

    public List<Group> getGroups(List<Integer> categoryIds) throws JSONException, IOException {
        List<Group> returnList = new ArrayList<>();
        List<String> items = new ArrayList<>();
        for (Integer categoryId : categoryIds) {
            items.addAll(pagedSearch(String.format(GROUP_URL, categoryId, "%s")));
        }
        for (String item : items) {
            returnList.addAll(mapper.readValue(item, new TypeReference<List<Group>>() {
            }));
        }
        return returnList;
    }

    public List<Product> getCardProducts(List<Integer> groupIds) throws JSONException, IOException {
        List<Product> returnList = new ArrayList<>();
        List<String> items = new ArrayList<>();
        for (int groupId : groupIds) {
            items.addAll(pagedSearch(String.format(PRODUCT_URL, groupId, "%s")));
        }

        for (String item : items) {
            returnList.addAll(mapper.readValue(item, new TypeReference<List<Product>>() {
            }));
        }
        return returnList;
    }

    public List<Sku> getSkus(List<Integer> productIds) throws JSONException, IOException {
        List<Sku> returnList = new ArrayList<>();
        List<String> items = new ArrayList<>();
        for (int productId : productIds) {
            items.add(singleSearch(String.format(SKU_URL, productId, "%s")));
        }

        for (String item : items) {
            returnList.addAll(mapper.readValue(item, new TypeReference<List<Sku>>() {
            }));
        }
        return returnList;
    }

    public List<Print> getPrints(List<Integer> categoryIds) throws JSONException, IOException {
        List<Print> returnList = new ArrayList<>();
        List<String> items = new ArrayList<>();

        for (int categoryId : categoryIds) {
            items.add(singleSearch(String.format(PRINTING_URL, categoryId, "%s")));
        }

        for (String item : items) {
            returnList.addAll(mapper.readValue(item, new TypeReference<List<Print>>() {
            }));
        }
        return returnList;
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


    private List<String> pagedSearch(String urlExtension) throws IOException, JSONException {
        int offset = 0;
        String url = String.format(BASE_URL, urlExtension);
        List<String> returnList = new ArrayList<>();
        while (true) {
            Request request = new Request.Builder()
                    .url(String.format(url, offset))
                    .get()
                    .addHeader(ACCEPT, ACCEPT_VALUE)
                    .addHeader(AUTHORIZATION, String.format(AUTHORIZATION_VALUE, bearerToken))
                    .build();

            Response response = client.newCall(request).execute();
            JSONObject body = new JSONObject(response.body().string());
            JSONArray results = body.getJSONArray("results");
            returnList.add(results.toString());
            int newOffest = offset + results.length();

            if (newOffest > offset) {
                offset = newOffest;
            }
            else {
                break;
            }
        }
        return returnList;
    }

    public attachUI(UI ui, LoadingView view) {
        
    }
}
