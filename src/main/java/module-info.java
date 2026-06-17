module com.grocerypos {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;

    exports com.grocerypos.app;

    opens com.grocerypos.model to com.fasterxml.jackson.databind;
    opens com.grocerypos.app to javafx.fxml;
    opens com.grocerypos.controller to javafx.fxml;
    opens com.grocerypos.controller.component to javafx.fxml;
}