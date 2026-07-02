package com.grocerypos.controller;

import com.grocerypos.model.Employee;
import com.grocerypos.service.EmployeeService;
import com.grocerypos.controller.component.NumpadController;
import com.grocerypos.controller.component.StatusBarController;
import com.grocerypos.controller.component.TopBarController;
import com.grocerypos.model.CartItem;
import com.grocerypos.service.CartService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import java.math.BigDecimal;

public class EmployeeController {

    // ── Component sub-controllers (injected by JavaFX from fx:include) ───────
    // Naming convention: fx:id="posNumpad" → field name "posNumpad" + "posNumpadController"

    @FXML private HBox topBar;
    @FXML private TopBarController topBarController;

    @FXML private HBox statusBar;
    @FXML private StatusBarController statusBarController;

    @FXML private GridPane posNumpad;
    @FXML private NumpadController posNumpadController;

    @FXML private GridPane credNumpad;
    @FXML private NumpadController credNumpadController;

    // ── Own FXML fields ──────────────────────────────────────────────────────

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
    @FXML private TableColumn<CartItem, String>     colName;
    @FXML private TableColumn<CartItem, Integer>    colQty;
    @FXML private TableColumn<CartItem, BigDecimal> colUnitPrice;
    @FXML private TableColumn<CartItem, BigDecimal> colLineTotal;

    private enum CredFocus { USERNAME, PASSWORD }
    private CredFocus credFocus = CredFocus.USERNAME;

    private CartService cartService;
    private final EmployeeService employeeService = new EmployeeService();
    private Employee currentEmployee;
    private CustomerController customerController;
    private final StringBuilder inputAccumulator = new StringBuilder();


    // ── Lifecycle ────────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        configureTableMapping();
        wireComponents();

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

    // ── Component wiring (equivalent to passing props) ───────────────────────

    private void wireComponents() {

        // TopBar — no callbacks needed, it manages its own clock
        // topBarController is ready to use immediately

        // StatusBar callbacks
        statusBarController.configure(
            () -> System.out.println("Invoking Peripheral Sequence Trigger: Open Drawer"),
            () -> System.out.println("Invoking Peripheral Sequence Trigger: Print Docket"),
            () -> System.out.println("Invoking Peripheral Sequence Trigger: User Barcode"),
            this::handleLogoutSequence
        );

        // POS numpad — quantity multiplier entry
        posNumpadController.configure(
            digit -> {
                inputAccumulator.append(digit);
                lblMultiplierScale.setText(inputAccumulator.toString());
            },
            this::handlePOSClear,
            this::handlePOSQtyApply,
            "QTY", "#e67e22",
            "CLR", "#95a5a6"
        );

        // Credential numpad — login PIN entry
        credNumpadController.configure(
            digit -> {
                if (credFocus == CredFocus.USERNAME) txtUsername.appendText(digit);
                else                                 txtPassword.appendText(digit);
            },
            this::handleCredClear,
            this::handleCredSubmit,
            "ENT", "#2ecc71",
            "CLR", "#e74c3c"
        );
    }

    // ── Table setup ──────────────────────────────────────────────────────────

    private void configureTableMapping() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colLineTotal.setCellValueFactory(new PropertyValueFactory<>("lineTotal"));
    }

    // ── State transitions ────────────────────────────────────────────────────

    @FXML void handleAdClick(MouseEvent event) {
        paneAd.setVisible(false);
        paneLogin.setVisible(true);
    }

    @FXML void handleLoginClick() {
        paneLogin.setVisible(false);
        paneCredentials.setVisible(true);
        credFocus = CredFocus.USERNAME;
    }

    private void handleCredClear() {
        txtUsername.clear();
        txtPassword.clear();
        credFocus = CredFocus.USERNAME;
    }

    private void handleCredSubmit() {
        String uid = txtUsername.getText();
        String pwd = txtPassword.getText();

        Employee result = employeeService.login(uid, pwd);

        if (result == null) {
            new Alert(Alert.AlertType.WARNING, "Login failed. Check PIN and password.").showAndWait();
            handleCredClear();
            return;
        }

        currentEmployee = result;
        topBarController.setOperatorName(currentEmployee.getFullName());

        paneCredentials.setVisible(false);
        paneLoading.setVisible(true);

        new Timeline(new KeyFrame(Duration.seconds(1.2), e -> {
            paneLoading.setVisible(false);
            paneMainPOS.setVisible(true);
            if (customerController != null) customerController.setCustomerStateActive(true);
        })).play();
    }

    // ── Main POS actions ─────────────────────────────────────────────────────

    private void handlePOSClear() {
        inputAccumulator.setLength(0);
        lblMultiplierScale.setText("1");
        if (cartService != null) cartService.setMultiplier(1);
    }

    private void handlePOSQtyApply() {
        if (inputAccumulator.length() > 0) {
            cartService.setMultiplier(Integer.parseInt(inputAccumulator.toString()));
            inputAccumulator.setLength(0);
        }
    }

    @FXML void handleManualScan() {
        if (txtScannerBuffer.getText() != null) processScan(txtScannerBuffer.getText().trim());
        txtScannerBuffer.clear();
    }

    /** Called by DebugController. Public so it can be reached from the separate debug window. */
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

    @FXML void handlePaymentAction(javafx.event.ActionEvent event) {
        String total = lblTotalBalance != null ? lblTotalBalance.getText() : "$0.00";
        new Alert(Alert.AlertType.INFORMATION,
                "Processing transaction via: " + ((Button) event.getSource()).getText()
                        + "\nTotal Settled: " + total).showAndWait();
        cartService.clearCart();
        if (lblTotalBalance != null) lblTotalBalance.setText("$0.00");
        if (customerController != null) customerController.syncDisplayView();
    }

    private void handleLogoutSequence() {
        employeeService.logout(currentEmployee);
        currentEmployee = null;

        paneMainPOS.setVisible(false);
        paneAd.setVisible(true);
        topBarController.setOperatorName("Not Authenticated");
        handlePOSClear();
        cartService.clearCart();
        if (customerController != null) customerController.setCustomerStateActive(false);
    }
}
