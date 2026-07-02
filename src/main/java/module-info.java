module com.grocerypos {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires org.aspectj.runtime; // Keep the standard runtime library, NOT the weaver!

    // Allows JavaFX to launch the application classes
    exports com.grocerypos.app;
    exports com.grocerypos.model;

    // Open your UI packages to JavaFX and AspectJ's runtime agents
    opens com.grocerypos.controller to javafx.fxml;
    opens com.grocerypos.controller.component to javafx.fxml;
    opens com.grocerypos.app to javafx.fxml;
    opens com.grocerypos.model to com.fasterxml.jackson.databind;
}