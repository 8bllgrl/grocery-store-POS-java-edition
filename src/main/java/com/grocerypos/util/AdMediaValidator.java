package com.grocerypos.util;

import javafx.scene.image.Image;
//import javafx.scene.media.Media;
import java.io.InputStream;

public class AdMediaValidator {

    /**
     * Evaluates a resource asset against an aspect ratio rule.
     * Returns true if valid, or logs a quiet error and returns false to trigger fallback.
     */
    public static boolean validateImageRatio(String path, double targetRatio) {
        try (InputStream is = AdMediaValidator.class.getResourceAsStream(path)) {
            if (is == null) {
                System.err.println("[QUIET ERROR] Ad asset not found at path: " + path + " -> Using fallback.");
                return false;
            }

            // Loading with background loading set to false to fetch size immediately
            Image img = new Image(is);
            if (img.isError()) {
                System.err.println("[QUIET ERROR] Failed to parse image payload: " + path + " -> Using fallback.");
                return false;
            }

            double actualRatio = img.getWidth() / img.getHeight();
            // Allow a tiny margin of floating point tolerance (e.g., 0.05)
            if (Math.abs(actualRatio - targetRatio) > 0.05) {
                System.err.printf("[QUIET ERROR] Aspect ratio mismatch for '%s'. Expected ~%.2f, got %.2f -> Using fallback.%n",
                        path, targetRatio, actualRatio);
                return false;
            }

            return true;
        } catch (Exception e) {
            System.err.println("[QUIET ERROR] Exception checking ad dimension matrix: " + e.getMessage() + " -> Using fallback.");
            return false;
        }
    }
}
