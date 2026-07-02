package com.grocerypos.controller;

import com.grocerypos.controller.component.ProductRowController;
import com.grocerypos.model.Product;
import com.grocerypos.service.CartService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DebugController {

    @FXML private Label lblDebugStatus;
    @FXML private VBox  catalogContainer;   // fx:id on the VBox that holds product rows

    private CartService cartService;
    private EmployeeController employeeController;

    public void initializeService(CartService service, EmployeeController empController) {
        this.cartService        = service;
        this.employeeController = empController;
        buildCatalogRows();
    }

    /**
     * Dynamically builds one ProductRow component per product.
     *
     * FIX: this now reads the catalog from CartService.getAllProducts() — the
     * same source of truth that powers real scans — instead of a hardcoded
     * local list. The old hardcoded List.of(...) was a second, independent
     * copy of the catalog that could silently drift out of sync with
     * products.json (different barcodes/names/prices), which is exactly what
     * caused the debug buttons to send barcodes the live CartService catalog
     * no longer recognised.
     */
    private void buildCatalogRows() {
        catalogContainer.getChildren().clear();

        List<Product> catalog = new ArrayList<>(cartService.getAllProducts());
        catalog.sort(Comparator.comparing(Product::getBarcode));

        if (catalog.isEmpty()) {
            lblDebugStatus.setText("> WARNING: catalog is empty — check products.json / module-info.java");
            return;
        }

        for (Product product : catalog) {
            try {
                FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/grocerypos/view/components/product_row.fxml"));
                Node row = loader.load();
                ProductRowController ctrl = loader.getController();
                ctrl.configure(product, this::fireScan);
                catalogContainer.getChildren().add(row);
            } catch (IOException e) {
                System.err.println("Failed to load product_row.fxml for: " + product.getName());
                e.printStackTrace();
            }
        }
    }

    private void fireScan(String barcode) {
        employeeController.triggerDebugScan(barcode);
        lblDebugStatus.setText("> scanned: [" + barcode + "]");
    }
}
