package com.grocerypos.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grocerypos.model.CartItem;
import com.grocerypos.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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

    public CartService() {
        loadCatalogFromJson();
    }

    private void loadCatalogFromJson() {
        ObjectMapper mapper = new ObjectMapper();

        // Dynamically extract data stream from application resources
        try (InputStream is = getClass().getResourceAsStream("/com/grocerypos/data/products.json")) {
            if (is == null) {
                System.err.println("Critical Error: products.json resource file not found!");
                return;
            }

            // Deserializing JSON list directly into a List of Product instances.
            // NOTE: this requires `opens com.grocerypos.model to com.fasterxml.jackson.databind;`
            // in module-info.java so Jackson can reflectively invoke the @JsonCreator
            // constructor. `exports` alone is not enough.
            List<Product> products = mapper.readValue(is, new TypeReference<List<Product>>() {});

            // Re-populate the live operational map
            for (Product p : products) {
                mockDatabase.put(p.getBarcode(), p);
            }

            System.out.println("[SYSTEM] Successfully parsed " + mockDatabase.size() + " items from JSON via Jackson.");

        } catch (Exception e) {
            // Surface the real cause loudly — an empty catalog otherwise fails
            // silently and every barcode scan looks like a "wrong barcode" bug
            // instead of "the catalog never loaded" bug.
            System.err.println("Critical Failure mapping local JSON matrix payload: "
                    + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Collection<Product> getAllProducts() {
        return mockDatabase.values();
    }

    public ObservableList<CartItem> getActiveCart() { return activeCart; }

    public void setMultiplier(int value) { this.currentMultiplier = value; }
    public int getCurrentMultiplier()    { return currentMultiplier; }

    public boolean scanItemByBarcode(String barcode) {
        Product p = mockDatabase.get(barcode);
        if (p == null) return false;

        // Real POS behaviour —
        //   • If a multiplier > 1 was explicitly set (quantity scan), add a new
        //     row with that explicit quantity, because the operator intentionally
        //     keyed a quantity before scanning.
        //   • If the multiplier is 1 (individual scan), always add a new row,
        //     even when the same barcode was scanned before. This mirrors how
        //     a physical scanner works: each trigger pull is a discrete scan event
        //     and produces its own receipt line at qty 1.
        if (currentMultiplier > 1) {
            activeCart.add(new CartItem(p, currentMultiplier));
        } else {
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
