package com.grocerypos.app;

import com.grocerypos.controller.CustomerController;
import com.grocerypos.controller.DebugController;
import com.grocerypos.controller.EmployeeController;
import com.grocerypos.service.CartService;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;

public class StageManager {

    private final CartService cartService = new CartService();

    public void launchDisplays(Stage primaryStage) {
        ObservableList<Screen> screens = Screen.getScreens();
        Screen primaryScreen = Screen.getPrimary();

        try {
            // ── Employee window ──────────────────────────────────────────────
            FXMLLoader empLoader = new FXMLLoader(
                    getClass().getResource("/com/grocerypos/view/employee_view.fxml"));
            Parent empRoot = empLoader.load();
            EmployeeController empController = empLoader.getController();
            empController.initializeService(cartService);

            primaryStage.setTitle("GroceryPOS Terminal - Employee Console");
            primaryStage.setScene(new Scene(empRoot));
            primaryStage.setX(primaryScreen.getVisualBounds().getMinX());
            primaryStage.setY(primaryScreen.getVisualBounds().getMinY());
            primaryStage.setMaximized(true);
            primaryStage.show();

            // ── Customer window ──────────────────────────────────────────────
            Stage customerStage = new Stage();
            FXMLLoader custLoader = new FXMLLoader(
                    getClass().getResource("/com/grocerypos/view/customer_view.fxml"));
            Parent custRoot = custLoader.load();
            CustomerController custController = custLoader.getController();
            custController.initializeService(cartService);

            customerStage.setTitle("GroceryPOS Terminal - Customer Facing Panel");
            customerStage.setScene(new Scene(custRoot));

            if (screens.size() > 1) {
                Screen secondary = screens.stream()
                        .filter(s -> !s.equals(primaryScreen))
                        .findFirst()
                        .orElse(primaryScreen);
                customerStage.setX(secondary.getVisualBounds().getMinX());
                customerStage.setY(secondary.getVisualBounds().getMinY());
                customerStage.setMaximized(true);
            } else {
                System.out.println("Single display: launching customer panel as floating window.");
                customerStage.setWidth(480);
                customerStage.setHeight(640);
                customerStage.setX(10);
                customerStage.setY(50);
                customerStage.setAlwaysOnTop(true);
            }

            primaryStage.setOnCloseRequest(event -> customerStage.close());
            customerStage.show();

            // Wire controllers together
            empController.setCustomerController(custController);

            // ── Debug window (only when launched with --debug) ───────────────
            if (Main.debugMode) {
                FXMLLoader dbgLoader = new FXMLLoader(
                        getClass().getResource("/com/grocerypos/view/debug_view.fxml"));
                Parent dbgRoot = dbgLoader.load();
                DebugController dbgController = dbgLoader.getController();
                dbgController.initializeService(cartService, empController);

                Stage debugStage = new Stage();
                debugStage.setTitle("GroceryPOS — Debug Console");
                debugStage.setScene(new Scene(dbgRoot));
                debugStage.setWidth(420);
                debugStage.setHeight(580);

                // Position it to the right of the employee window on single-monitor setups
                debugStage.setX(primaryScreen.getVisualBounds().getMaxX() - 440);
                debugStage.setY(primaryScreen.getVisualBounds().getMinY() + 40);
                debugStage.setAlwaysOnTop(true);
                debugStage.setResizable(false);

                // Close debug window when the main window closes
                primaryStage.setOnCloseRequest(event -> {
                    customerStage.close();
                    debugStage.close();
                });

                debugStage.show();
                System.out.println("[DEBUG] Debug console active.");
            }

        } catch (IOException e) {
            System.err.println("Critical Error loading layout profiles.");
            e.printStackTrace();
        }
    }
}