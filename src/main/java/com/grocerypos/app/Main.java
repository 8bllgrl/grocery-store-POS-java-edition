package com.grocerypos.app;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    /** Set to true when the app is launched with the --debug argument. */
    static boolean debugMode = false;

    private StageManager stageManager;

    @Override
    public void init() {
        // getParameters() is only valid inside init() / start() — not in main().
        // getRaw() contains the args exactly as passed on the command line.
        debugMode = getParameters().getRaw().contains("--debug");
        if (debugMode) System.out.println("[DEBUG] Debug mode enabled.");
        stageManager = new StageManager();
    }

    @Override
    public void start(Stage primaryStage) {
        stageManager.launchDisplays(primaryStage);
    }

    public static void main(String[] args) {
        // launch() hands args to the JavaFX runtime, which makes them available
        // via getParameters() inside init() above.
        launch(args);
    }
}