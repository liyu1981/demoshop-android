package com.facebook.se.apac.example.liyuhk.didiadsa;

import android.content.Intent;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

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
    
    float calculateTotalPrice() {
        float total = 0;
        for (HashMap<String, String> p: cart) {
            String pricestr = p.get(MainActivity.FEED_TAG_ENTRY_PRICE);
            String[] parts = pricestr.split("\\s+");
            float price = Float.parseFloat(parts[0].replace("," , "."));
            total += price;
        }
        return total;
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
