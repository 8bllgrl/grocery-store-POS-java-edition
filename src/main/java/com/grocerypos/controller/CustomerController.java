package com.grocerypos.controller;

import com.grocerypos.model.CartItem;
import com.grocerypos.service.CartService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.math.BigDecimal;

public class CustomerController {

    @FXML private VBox customerIdlePane;
    @FXML private VBox customerActivePane;
    @FXML private ListView<CartItem> listItemsView;
    @FXML private Label lblCustomerGrandTotal; // Must match fx:id
    private CartService cartService;

    public void initializeService(CartService service) {
        this.cartService = service;
        listItemsView.setItems(cartService.getActiveCart());

        // FIX: ListView renders each item via toString() by default, and
        // CartItem never overrides it — hence "com.grocerypos.model.CartItem@33c9967"
        // showing up instead of the product name/qty/price. A cellFactory
        // tells the ListView exactly how to render a CartItem.
        listItemsView.setCellFactory(buildCartItemCellFactory());

        syncDisplayView();
    }

    private Callback<ListView<CartItem>, ListCell<CartItem>> buildCartItemCellFactory() {
        return lv -> new ListCell<>() {
            @Override
            protected void updateItem(CartItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%-28s x%-3d $%s",
                            item.getName(), item.getQuantity(), item.getLineTotal()));
                }
            }
        };
    }

    /**
     * Drives all updates from the shared CartService to the customer display interface.
     * Toggles layout profiles based on whether the active cart has rows or is empty.
     */
    public void syncDisplayView() {
        if (cartService == null || cartService.getActiveCart().isEmpty()) {
            customerActivePane.setVisible(false);
            customerIdlePane.setVisible(true);
        } else {
            customerIdlePane.setVisible(false);
            customerActivePane.setVisible(true);

            // Re-render and print literal metric summaries
            BigDecimal total = cartService.getGrandTotal();
            lblCustomerGrandTotal.setText("$" + total.toString());
        }
    }

    /**
     * Programmatic backup handle to explicitly override state triggers.
     */
    public void setCustomerStateActive(boolean active) {
        if (active) {
            customerIdlePane.setVisible(false);
            customerActivePane.setVisible(true);
        } else {
            customerActivePane.setVisible(false);
            customerIdlePane.setVisible(true);
        }
    }
}
