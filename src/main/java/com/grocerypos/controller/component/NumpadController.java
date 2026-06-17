package com.grocerypos.controller.component;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.util.function.Consumer;

/**
 * Component controller for the shared numeric keypad.
 *
 * Usage — after fx:include loads this controller, call configure() from the
 * parent controller's initialize() to inject callbacks and customise the two
 * action buttons (the clear key and the right-hand confirm key).
 *
 * Equivalent to passing props to a React component.
 */
public class NumpadController {

    @FXML private Button btnClear;
    @FXML private Button btnAction;

    private Consumer<String> onDigit;
    private Runnable onClear;
    private Runnable onAction;

    /**
     * "Props" — called by the parent controller after the include is wired.
     *
     * @param onDigit      callback receiving the digit string ("0"–"9")
     * @param onClear      callback for the CLR button
     * @param onAction     callback for the right confirm button (ENT / QTY / etc.)
     * @param actionLabel  text shown on the confirm button
     * @param actionColor  JavaFX CSS colour string for the confirm button background
     * @param clearLabel   text shown on the clear button (default "CLR")
     * @param clearColor   JavaFX CSS colour string for the clear button background
     */
    public void configure(
            Consumer<String> onDigit,
            Runnable onClear,
            Runnable onAction,
            String actionLabel, String actionColor,
            String clearLabel,  String clearColor) {

        this.onDigit  = onDigit;
        this.onClear  = onClear;
        this.onAction = onAction;

        btnAction.setText(actionLabel);
        btnAction.setStyle("-fx-background-color: " + actionColor + "; -fx-text-fill: white;");
        btnClear.setText(clearLabel);
        btnClear.setStyle("-fx-background-color: " + clearColor + "; -fx-text-fill: white;");
    }

    // ── FXML handlers ────────────────────────────────────────────────────────

    @FXML
    void handleNum(ActionEvent e) {
        if (onDigit != null) onDigit.accept(((Button) e.getSource()).getText());
    }

    @FXML
    void handleClear() {
        if (onClear != null) onClear.run();
    }

    @FXML
    void handleAction() {
        if (onAction != null) onAction.run();
    }
}
