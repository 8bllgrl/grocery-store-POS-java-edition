package com.grocerypos.view;

import com.grocerypos.app.StageManager;
import com.grocerypos.service.CartService;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerDisplayTransparencyTest extends ApplicationTest {

    private StageManager stageManager;
    private CartService operationalCartService;

    @BeforeAll
    public static void setupHeadlessMode() {
        // Initializes the native JavaFX subsystem on Java 24 without requiring Monocle
        Platform.startup(() -> {});
    }

    @Override
    public void start(Stage stage) throws Exception {
        stageManager = new StageManager();
        stageManager.launchDisplays(stage);
        operationalCartService = stageManager.getCartService();

        Platform.runLater(() -> operationalCartService.clearCart());
        interact(() -> {});
    }

    @Test
    public void testCustomerPaneSynchronizesAndShowsExactTotals() {
        // ── STEP 1: DIRECT STATE MUTATION & UI SYNCHRONIZATION ──
        interact(() -> {
            // 1. Force state modification via the backend service directly
            boolean success = operationalCartService.scanItemByBarcode("444");
            assertTrue(success, "The test barcode '444' must exist inside the CartService catalog.");

            // 2. CRITICAL FIX: Explicitly command the customer display controller
            // to process the new data state and swap panels right now!
            stageManager.getCustomerController().syncDisplayView();
        });

        // ── STEP 2: FLUSH RENDER LOOP PIPELINE ──
        // Ensure JavaFX thread completely finishes drawing the new layout trees
        WaitForAsyncUtils.waitForFxEvents();

        // ── STEP 3: ASSERTS RUN INSTANTLY AT WARP SPEED ──
        ListView<?> customerCartList = lookup("#listItemsView").queryListView();
        Label customerTotalLabel = lookup("#lblCustomerGrandTotal").queryAs(Label.class);

        int backendItemCount = operationalCartService.getActiveCart().size();
        BigDecimal backendCalculatedTotal = operationalCartService.getGrandTotal();

        // 1. Audit hidden fees / phantom items
        assertEquals(backendItemCount, customerCartList.getItems().size(),
                "FRAUD EXPLOIT DETECTED: Customer interface rows diverge from backend cart lengths!");

        // 2. Audit literal output values
        String displayedText = customerTotalLabel.getText();
        assertNotNull(displayedText, "The customer grand total pricing text label must not be null.");

        assertTrue(displayedText.contains(backendCalculatedTotal.toString()),
                "HIDDEN FEE DETECTED: The checkout interface value ($" + displayedText
                        + ") deviates from backend transactional metrics ($" + backendCalculatedTotal + ")!");
    }
}