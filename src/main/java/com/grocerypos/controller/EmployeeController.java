package com.grocerypos.controller;

import com.grocerypos.model.CartItem;
import com.grocerypos.service.CartService;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class EmployeeController {

    @FXML private Label lblClock;
    @FXML private Label lblEmployee;
    @FXML private Label lblMultiplierScale;
    @FXML private Label lblTotalBalance;

    @FXML private javafx.scene.layout.Pane paneAd;
    @FXML private javafx.scene.layout.Pane paneLogin;
    @FXML private javafx.scene.layout.Pane paneCredentials;
    @FXML private javafx.scene.layout.Pane paneLoading;
    @FXML private javafx.scene.layout.Pane paneMainPOS;

    @FXML private TextField txtUsername;
    @FXML private TextField txtScannerBuffer;
    @FXML private PasswordField txtPassword;

    @FXML private TableView<CartItem> tableCart;
    @FXML private TableColumn<CartItem, String> colName;
    @FXML private TableColumn<CartItem, Integer> colQty;
    @FXML private TableColumn<CartItem, BigDecimal> colUnitPrice;
    @FXML private TableColumn<CartItem, BigDecimal> colLineTotal;

    // Tracks which credential field the numpad should write to, since
    // clicking a button steals JavaFX focus before the handler fires.
    private enum CredFocus { USERNAME, PASSWORD }
    private CredFocus credFocus = CredFocus.USERNAME;

    private CartService cartService;
    private CustomerController customerController;
    private final StringBuilder inputAccumulator = new StringBuilder();

    @FXML
    public void initialize() {
        startClockRunner();
        configureTableMapping();

        // Keep credFocus in sync when the user directly clicks a field.
        txtUsername.setOnMouseClicked(e -> credFocus = CredFocus.USERNAME);
        txtPassword.setOnMouseClicked(e -> credFocus = CredFocus.PASSWORD);
    }

    public void initializeService(CartService service) {
        this.cartService = service;
        tableCart.setItems(cartService.getActiveCart());
    }

    public void setCustomerController(CustomerController target) {
        this.customerController = target;
    }

    private void startClockRunner() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (lblClock != null) lblClock.setText(LocalTime.now().format(dtf));
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void configureTableMapping() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colLineTotal.setCellValueFactory(new PropertyValueFactory<>("lineTotal"));
    }

    // ── STATE TRANSITIONS ────────────────────────────────────────────────────

    @FXML void handleAdClick(MouseEvent event) {
        paneAd.setVisible(false);
        paneLogin.setVisible(true);
    }

    @FXML void handleLoginClick(ActionEvent event) {
        paneLogin.setVisible(false);
        paneCredentials.setVisible(true);
        credFocus = CredFocus.USERNAME;
    }

    @FXML void handleCredNum(ActionEvent event) {
        Button btn = (Button) event.getSource();
        if (credFocus == CredFocus.USERNAME) txtUsername.appendText(btn.getText());
        else                                 txtPassword.appendText(btn.getText());
    }

    @FXML void handleCredClear() {
        txtUsername.clear();
        txtPassword.clear();
        credFocus = CredFocus.USERNAME;
    }

    @FXML void handleCredSubmit() {
        String uid = txtUsername.getText();
        if (uid == null || uid.isEmpty()) uid = "Teller #104";
        lblEmployee.setText("Operator: " + uid);

        paneCredentials.setVisible(false);
        paneLoading.setVisible(true);

        new Timeline(new KeyFrame(Duration.seconds(1.2), e -> {
            paneLoading.setVisible(false);
            paneMainPOS.setVisible(true);
            if (customerController != null) customerController.setCustomerStateActive(true);
        })).play();
    }

    // ── MAIN POS ACTIONS ────────────────────────────────────────────────────

    @FXML void handlePOSNum(ActionEvent event) {
        Button btn = (Button) event.getSource();
        inputAccumulator.append(btn.getText());
        lblMultiplierScale.setText(inputAccumulator.toString());
    }

    @FXML void handlePOSClear() {
        inputAccumulator.setLength(0);
        lblMultiplierScale.setText("1");
        if (cartService != null) cartService.setMultiplier(1);
    }

    @FXML void handlePOSQtyApply() {
        if (inputAccumulator.length() > 0) {
            cartService.setMultiplier(Integer.parseInt(inputAccumulator.toString()));
            inputAccumulator.setLength(0);
        }
    }

    @FXML void handleManualScan() {
        if (txtScannerBuffer.getText() != null) processScan(txtScannerBuffer.getText().trim());
        txtScannerBuffer.clear();
    }

    /**
     * Called by DebugController when a catalog button is pressed.
     * Public so it can be reached from the separate debug window.
     */
    public void triggerDebugScan(String barcode) {
        processScan(barcode);
    }

    private void processScan(String barcode) {
        boolean success = cartService.scanItemByBarcode(barcode);
        if (success) {
            tableCart.refresh();
            lblTotalBalance.setText(String.format("$%.2f", cartService.getGrandTotal()));
            lblMultiplierScale.setText("1");
            if (customerController != null) customerController.syncDisplayView();
        } else {
            new Alert(Alert.AlertType.WARNING, "Barcode value not found in local JSON matrix.").showAndWait();
        }
    }

    @FXML void handlePaymentAction(ActionEvent event) {
        String total = lblTotalBalance != null ? lblTotalBalance.getText() : "$0.00";
        new Alert(Alert.AlertType.INFORMATION,
                "Processing transaction via: " + ((Button) event.getSource()).getText()
                        + "\nTotal Settled: " + total).showAndWait();
        cartService.clearCart();
        if (lblTotalBalance != null) lblTotalBalance.setText("$0.00");
        if (customerController != null) customerController.syncDisplayView();
    }

    @FXML void handleDockAction(ActionEvent event) {
        System.out.println("Invoking Peripheral Sequence Trigger: " + ((Button) event.getSource()).getText());
    }

    @FXML void handleLogoutSequence() {
        paneMainPOS.setVisible(false);
        paneAd.setVisible(true);
        lblEmployee.setText("Operator: Not Authenticated");
        handlePOSClear();
        cartService.clearCart();
        if (customerController != null) customerController.setCustomerStateActive(false);
    }
}