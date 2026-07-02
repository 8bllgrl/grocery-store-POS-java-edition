package com.grocerypos.service;

import com.grocerypos.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

public class CartServiceTransparencyTest {

    private CartService cartService;

    @BeforeEach
    public void setUp() {
        cartService = new CartService();
        // Clear any previous static data or state
        cartService.clearCart();
    }

    @Test
    public void testNoHiddenCostsOnIndividualAndQuantityScans() {
        // Given a known catalog item
        String itemBarcode = "444"; // e.g., Cadbury Dairy Milk
        BigDecimal individualPrice = new BigDecimal("5.50");

        // When a product is scanned individually
        cartService.scanItemByBarcode(itemBarcode);

        // Then total must match exactly
        assertEquals(individualPrice, cartService.getGrandTotal(),
                "CRITICAL: Base cart total must equal the individual item price precisely.");

        // When a quantity multiplier of 4 is set
        cartService.setMultiplier(4);
        cartService.scanItemByBarcode(itemBarcode);

        // Expected calculation: 1 item ($5.50) + 4 items ($22.00) = $27.50
        BigDecimal expectedTotal = individualPrice.multiply(new BigDecimal("5"));

        assertEquals(expectedTotal, cartService.getGrandTotal(),
                "ANTI-FRAUD: Calculated sum must be perfectly transparent across multiple additions.");

        // Assert multiplier safely resets to 1 to avoid accidental trailing costs on subsequent items
        cartService.scanItemByBarcode(itemBarcode);
        BigDecimal postTotal = expectedTotal.add(individualPrice);
        assertEquals(postTotal, cartService.getGrandTotal(),
                "SECURITY: Multiplier did not stick; subsequent scan processed cleanly at quantity 1.");
    }
}
