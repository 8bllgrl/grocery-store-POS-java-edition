package com.grocerypos.controller;

import com.grocerypos.model.CartItem;
import com.grocerypos.service.CartService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

public class CustomerController {

    @FXML private VBox customerIdlePane, customerActivePane;
    @FXML private ListView<String> listItemsView;
    @FXML private Label lblCustomerTotal;

    private CartService cartService;

    public void initializeService(CartService service) {
        this.cartService = service;
    }

    // BUG FIX 2: The original only called syncDisplayView() when going *inactive*,
    // which meant the customer screen never populated when the transaction started.
    // Now syncDisplayView() is called on both transitions so the display is always
    // up-to-date regardless of which direction the state switches.
    public void setCustomerStateActive(boolean active) {
        customerIdlePane.setVisible(!active);
        customerActivePane.setVisible(active);
        syncDisplayView();
    }

    public void syncDisplayView() {
        listItemsView.getItems().clear();
        for (CartItem item : cartService.getActiveCart()) {
            String formattedLine = String.format("%-22s x%-3d  $%7.2f",
                    item.getName(),
                    item.getQuantity(),
                    item.getLineTotal());
            listItemsView.getItems().add(formattedLine);
        }
        lblCustomerTotal.setText(String.format("$%.2f", cartService.getGrandTotal()));
    }
}