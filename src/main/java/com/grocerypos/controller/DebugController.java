package com.grocerypos.controller;

import com.grocerypos.service.CartService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DebugController {

    @FXML private Label lblDebugStatus;

    private CartService cartService;
    private EmployeeController employeeController;

    public void initializeService(CartService service, EmployeeController empController) {
        this.cartService = service;
        this.employeeController = empController;
    }

    @FXML void scanMuesli(ActionEvent e)   { fireScan("111", "WW Muesli Bars 6pk"); }
    @FXML void scanMilk(ActionEvent e)     { fireScan("222", "Full Cream Milk 2L"); }
    @FXML void scanChips(ActionEvent e)    { fireScan("333", "Thick Cut Chips 175g"); }
    @FXML void scanDairyMilk(ActionEvent e){ fireScan("444", "Cadbury Dairy Milk"); }

    private void fireScan(String barcode, String name) {
        // Delegate to EmployeeController so the main cart table, total label,
        // and customer screen all update exactly as a real scan would.
        employeeController.triggerDebugScan(barcode);
        lblDebugStatus.setText("> scanned: [" + barcode + "] " + name);
    }
}