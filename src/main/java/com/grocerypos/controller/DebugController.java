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
import java.math.BigDecimal;
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
     * Dynamically builds one ProductRow component per product — replaces the
     * four hardcoded HBox blocks that were in the original FXML.
     */
    private void buildCatalogRows() {
        // In a real app this list would come from CartService/ProductRepository.
        List<Product> catalog = List.of(
            new Product("111", "WW Muesli Bars 6pk",  new BigDecimal("4.50")),
            new Product("222", "Full Cream Milk 2L",   new BigDecimal("3.10")),
            new Product("333", "Thick Cut Chips 175g", new BigDecimal("4.80")),
            new Product("444", "Cadbury Dairy Milk",   new BigDecimal("5.50"))
        );

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
