module com.grocerypos {
    // 1. Core modules required to run the application
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;

    // 2. EXPORTS: Allows javafx.graphics to instantiate your application launcher classes
    exports com.grocerypos.app;

    // 3. OPENS: Grants deep reflection privileges to third-party framework layers
    opens com.grocerypos.model to com.fasterxml.jackson.databind;
    opens com.grocerypos.app to javafx.fxml;
    opens com.grocerypos.controller to javafx.fxml;
    opens com.grocerypos.controller.component to javafx.fxml;
}