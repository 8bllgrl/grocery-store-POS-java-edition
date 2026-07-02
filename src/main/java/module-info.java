module com.grocerypos {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;

    // Allows JavaFX to see and launch the application classes
    exports com.grocerypos.app;

    // FIX: both Jackson AND JavaFX need REFLECTIVE access to this package —
    // `exports` only grants compile-time visibility, it does NOT permit
    // reflection, so this must be `opens`:
    //   • com.fasterxml.jackson.databind reflectively invokes the
    //     @JsonCreator constructor on Product when reading products.json.
    //     Without this, the catalog never loads and every barcode scan
    //     fails with "Barcode value not found in local JSON matrix."
    //   • javafx.base reflectively calls nameProperty()/quantityProperty()/
    //     etc. on CartItem via PropertyValueFactory (used by the cart
    //     TableColumns in EmployeeController). Without this, the table
    //     throws IllegalAccessException for every cell and every row in
    //     the employee cart table renders completely empty.
    opens com.grocerypos.model to com.fasterxml.jackson.databind, javafx.base;

    opens com.grocerypos.controller to javafx.fxml;
    opens com.grocerypos.controller.component to javafx.fxml;
    opens com.grocerypos.app to javafx.fxml;
}
