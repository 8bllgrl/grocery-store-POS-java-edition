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

    // FIX: keep the barcode as data, not just text baked into a label.
    // The previous version re-parsed it out of lblBarcodeName's displayed
    // string ("[111]  Name" -> substring before "]"), which breaks the
    // moment the label format ever changes (e.g. adding an icon prefix).
    private String barcode;
    private Consumer<String> onScan;

    public void configure(Product product, Consumer<String> onScan) {
        this.onScan = onScan;
        this.barcode = product.getBarcode();
        lblBarcodeName.setText("[" + product.getBarcode() + "]  " + product.getName());
        lblPrice.setText("       /$" + product.getUnitPrice() + " / unit");
    }

    @FXML
    void handleScan() {
        if (onScan != null && barcode != null) onScan.accept(barcode);
    }
}
