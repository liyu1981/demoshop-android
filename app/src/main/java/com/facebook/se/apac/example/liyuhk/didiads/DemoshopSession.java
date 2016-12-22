package com.facebook.se.apac.example.liyuhk.didiads;

import android.content.Intent;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Float.*;

class DemoshopSession {

    HashMap<String, String> selectedProduct = null;
    ArrayList<HashMap<String, String>> cart = new ArrayList<>();
    String selectedCategory = "ALL";

    void saveToIntent(Intent i) {
        if (selectedProduct != null) {
            i.putExtra("selectedProduct", selectedProduct);
        }
        i.putExtra("cart", cart);
        if (selectedCategory != null) {
            i.putExtra("selectedCategory", selectedCategory);
        }
    }

    void clearSelectedProduct() {
        selectedProduct = null;
    }

    void addSelectedProductToCart() {
        cart.add(selectedProduct);
        selectedProduct = null;
    }

    void payAll() {
        selectedProduct = null;
        cart.clear();
    }

    static float calculateProductPrice(HashMap<String, String> p) {
        String pricestr = p.get(MainActivity.FEED_TAG_ENTRY_PRICE);
        String[] parts = pricestr.split("\\s+");
        return parseFloat(parts[0].replace("," , "."));
    }
    
    float calculateTotalPrice() {
        float total = 0;
        for (HashMap<String, String> p: cart) {
            total += calculateProductPrice(p);
        }
        return total;
    }

    ArrayList<String> calculateCartContentIDs() {
        ArrayList<String> list = new ArrayList<>();
        for (HashMap<String, String> p: cart) {
            list.add(p.get(MainActivity.FEED_TAG_ENTRY_ID));
        }
        return list;
    }

    String calculateCartContentIDsJSONStr() {
        ArrayList<String> list = calculateCartContentIDs();
        JSONArray jsArray = new JSONArray(list);
        return jsArray.toString();
    }

    static DemoshopSession ExtractFromIntent(Intent i) {
        DemoshopSession ds = new DemoshopSession();
        if (i.hasExtra("selectedProduct")) {
            ds.selectedProduct = (HashMap<String, String>)i.getSerializableExtra("selectedProduct");
        }
        if (i.hasExtra("cart")) {
            ds.cart.addAll((ArrayList<HashMap<String, String>>)i.getSerializableExtra("cart"));
        }
        if (i.hasExtra("selectedCategory")) {
            ds.selectedCategory = (String)i.getSerializableExtra("selectedCategory");
        }
        return ds;
    }
}
