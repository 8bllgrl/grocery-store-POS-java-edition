module com.grocerypos {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires org.aspectj.runtime;
    requires java.sql;
    requires org.postgresql.jdbc;

    exports com.grocerypos.app;
    exports com.grocerypos.model;

    opens com.grocerypos.controller to javafx.fxml;
    opens com.grocerypos.controller.component to javafx.fxml;
    opens com.grocerypos.app to javafx.fxml;
    opens com.grocerypos.model to com.fasterxml.jackson.databind;
}