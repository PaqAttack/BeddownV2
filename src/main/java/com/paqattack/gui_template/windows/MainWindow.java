package com.example.updated_beddown.windows;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.util.logging.Logger;

public class MainWindow extends BorderPane {
    private static final Logger logger = Logger.getLogger(MainWindow.class.getName());

    @FXML public Label myLabel;
    @FXML public Button myButton;

    public MainWindow() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(WindowUtils.FXML_PATH + "MainWindow.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            myButton.setOnAction(event -> onHelloButtonClick());
            logger.info("FXML file (MainWindow) loaded successfully");
        } catch (Exception e) {
            logger.warning("Error loading FXML file (MainWindow.fxml): " + e.getMessage());
        }
    }

    public void onHelloButtonClick() {
        myLabel.setText("Welcome to JavaFX Application!");
        Sidebar sidebar = new Sidebar();
        setLeft(sidebar);
        sidebar.setLabelText("Hello from the first window!");
        logger.info("Hello button clicked!");
    }
}
