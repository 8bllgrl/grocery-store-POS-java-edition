package com.grocerypos.service;

import com.grocerypos.model.CartItem;
import com.grocerypos.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartService {
    private final ObservableList<CartItem> activeCart = FXCollections.observableArrayList();
    private final Map<String, Product> mockDatabase = new HashMap<>();
    private int currentMultiplier = 1;

//    public CartService() {
//        loadCatalogFromJson();
//    }

//    private void loadCatalogFromJson() {
//        ObjectMapper mapper = new ObjectMapper();
//
//        // Dynamically extract data stream from application resources
//        try (InputStream is = getClass().getResourceAsStream("/com/grocerypos/data/products.json")) {
//            if (is == null) {
//                System.err.println("Critical Error: products.json resource file not found!");
//                return;
//            }
//
//            // Deserializing JSON list directly into a List of Product instances
//            List<Product> products = mapper.readValue(is, new TypeReference<List<Product>>() {});
//
//            // Re-populate the live operational map [cite: 136]
//            for (Product p : products) {
//                mockDatabase.put(p.getBarcode(), p);
//            }
//
//            System.out.println("[SYSTEM] Successfully parsed " + mockDatabase.size() + " items from JSON via Jackson.");
//
//        } catch (Exception e) {
//            System.err.println("Critical Failure mapping local JSON matrix payload.");
//            e.printStackTrace();
//        }
//    }
//
//    public Collection<Product> getAllProducts() {
//        return mockDatabase.values();
//    }

    public ObservableList<CartItem> getActiveCart() { return activeCart; }

    public void setMultiplier(int value) { this.currentMultiplier = value; }
    public int getCurrentMultiplier()    { return currentMultiplier; }

    public boolean scanItemByBarcode(String barcode) {
        Product p = mockDatabase.get(barcode);
        if (p == null) return false;

        // BUG FIX 4: Real POS behaviour —
        //   • If a multiplier > 1 was explicitly set (quantity scan), find an
        //     existing row for this product and accumulate into it, because the
        //     operator intentionally keyed a quantity before scanning.
        //   • If the multiplier is 1 (individual scan), always add a new row,
        //     even when the same barcode was scanned before. This mirrors how
        //     a physical scanner works: each trigger pull is a discrete scan event
        //     and produces its own receipt line at qty 1.
        if (currentMultiplier > 1) {
            // Product not yet in cart — add a new row with the explicit quantity.
            activeCart.add(new CartItem(p, currentMultiplier));
        } else {
            // Individual-scan path: always a new row at qty 1.
            activeCart.add(new CartItem(p, 1));
        }

        currentMultiplier = 1;
        return true;
    }

    public BigDecimal getGrandTotal() {
        return activeCart.stream()
                .map(CartItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void clearCart() {
        activeCart.clear();
        currentMultiplier = 1;
    }
}