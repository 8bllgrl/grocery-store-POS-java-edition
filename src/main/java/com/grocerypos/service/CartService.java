package com.grocerypos.service;

import com.grocerypos.model.CartItem;
import com.grocerypos.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class CartService {
    private final ObservableList<CartItem> activeCart = FXCollections.observableArrayList();
    private final Map<String, Product> mockDatabase = new HashMap<>();
    private int currentMultiplier = 1;

    public CartService() {
        mockDatabase.put("111", new Product("111", "WW Muesli Bars 6pk",   new BigDecimal("4.50")));
        mockDatabase.put("222", new Product("222", "Full Cream Milk 2L",    new BigDecimal("3.10")));
        mockDatabase.put("333", new Product("333", "Thick Cut Chips 175g",  new BigDecimal("4.80")));
        mockDatabase.put("444", new Product("444", "Cadbury Dairy Milk",    new BigDecimal("5.50")));
    }

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
            // Quantity-scan path: accumulate into the existing row if present.
//            for (CartItem item : activeCart) {
//                if (item.getName().equals(p.getName())) {
//                    item.setQuantity(item.getQuantity() + currentMultiplier);
//                    currentMultiplier = 1;
//                    return true;
//                }
//            }
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