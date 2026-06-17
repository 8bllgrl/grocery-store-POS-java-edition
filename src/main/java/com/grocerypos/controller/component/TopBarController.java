package com.grocerypos.controller.component;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Component controller for the top application bar.
 *
 * Owns the clock ticker. The parent controller calls setOperatorName() to
 * update the operator label after login/logout.
 */
public class TopBarController {

    @FXML private Label lblEmployee;
    @FXML private Label lblClock;

    @FXML
    public void initialize() {
        startClockRunner();
    }

    public void setOperatorName(String name) {
        lblEmployee.setText("Operator: " + name);
    }

    private void startClockRunner() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (lblClock != null) lblClock.setText(LocalTime.now().format(dtf));
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
}
