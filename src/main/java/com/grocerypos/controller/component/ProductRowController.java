package com.grocerypos.controller.component;

import com.grocerypos.model.Product;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.util.function.Consumer;

/**
 * Component controller for a single product row in the debug catalog.
 *
 * Used by DebugController: one FXMLLoader per product, then configure() is
 * called to bind the product data and scan callback.
 */
public class ProductRowController {

    @FXML private Label lblBarcodeName;
    @FXML private Label lblPrice;
    @FXML private Button btnScan;

    private Consumer<String> onScan;

    public void configure(Product product, Consumer<String> onScan) {
        this.onScan = onScan;
        lblBarcodeName.setText("[" + product.getBarcode() + "]  " + product.getName());
        lblPrice.setText("       /$" + product.getUnitPrice() + " / unit");
    }

    @FXML
    void handleScan() {
        // Extract barcode from the label text — format is "[111]  Name"
        String text = lblBarcodeName.getText();
        String barcode = text.substring(1, text.indexOf(']'));
        if (onScan != null) onScan.accept(barcode);
    }
}
