package com.grocerypos.service;

import com.grocerypos.model.CartItem;
import com.grocerypos.model.Product;
import com.grocerypos.repository.ProductRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
        loadCatalogFromDatabase();
    }

    private void loadCatalogFromDatabase() {
        ProductRepository repository = new ProductRepository();
        List<Product> products = repository.findAll();

        if (products.isEmpty()) {
            System.err.println("Critical Error: no products loaded from database!");
            return;
        }

        for (Product p : products) {
            mockDatabase.put(p.getBarcode(), p);
        }

        System.out.println("[SYSTEM] Successfully loaded " + mockDatabase.size() + " items from PostgreSQL.");
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
