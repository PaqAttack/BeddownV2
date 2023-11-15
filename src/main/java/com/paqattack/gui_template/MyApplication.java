package com.paqattack.gui_template;

import com.paqattack.gui_template.windows.AnchorPaneMain;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class MyApplication extends Application {
    private static final Logger logger = Logger.getLogger(MyApplication.class.getName());
    @Override
    public void start(Stage stage) {

        //TODO create manager for this!

        //MainWindow mainWindow = new MainWindow();
        AnchorPaneMain mainWindow = new AnchorPaneMain();
        stage.setScene(new Scene(mainWindow));
        stage.setTitle("Hello!");
        stage.show();
    }

    public static void main(String[] args) {
        try {
            LogManager.getLogManager().readConfiguration(MyApplication.class.getResourceAsStream("logging.properties"));
        } catch (IOException e) {
            logger.warning("Error loading logging configuration: " + e.getMessage());
        }
        launch();
    }
}