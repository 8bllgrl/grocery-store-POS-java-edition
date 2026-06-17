package com.grocerypos.controller.component;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 * Component controller for the bottom status / dock bar.
 *
 * The parent injects callbacks for each button action via configure().
 */
public class StatusBarController {

    private Runnable onOpenDrawer;
    private Runnable onPrintDocket;
    private Runnable onUserBarcode;
    private Runnable onLogout;

    public void configure(
            Runnable onOpenDrawer,
            Runnable onPrintDocket,
            Runnable onUserBarcode,
            Runnable onLogout) {
        this.onOpenDrawer  = onOpenDrawer;
        this.onPrintDocket = onPrintDocket;
        this.onUserBarcode = onUserBarcode;
        this.onLogout      = onLogout;
    }

    @FXML void handleOpenDrawer(ActionEvent e)  { if (onOpenDrawer  != null) onOpenDrawer.run(); }
    @FXML void handlePrintDocket(ActionEvent e) { if (onPrintDocket != null) onPrintDocket.run(); }
    @FXML void handleUserBarcode(ActionEvent e) { if (onUserBarcode != null) onUserBarcode.run(); }
    @FXML void handleLogout(ActionEvent e)      { if (onLogout      != null) onLogout.run(); }
}
