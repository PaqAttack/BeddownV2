package com.paqattack.gui_template;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class MyApplication extends Application {
    private static final Logger logger = Logger.getLogger(MyApplication.class.getName());
    @Override
    public void start(Stage stage) {
        WindowManager windowManager = new WindowManager(stage);
        windowManager.start();
    }

    public static void main(String[] args) {
        try {
            LogManager.getLogManager().readConfiguration(MyApplication.class.getResourceAsStream("/logging.properties"));
        } catch (IOException e) {
            logger.warning("Error loading logging configuration: " + e.getMessage());
        }
        launch();
    }
}