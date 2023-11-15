package com.paqattack.gui_template.windows;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.util.logging.Logger;

public class AnchorPaneMain  extends AnchorPane {

    private static final Logger logger = Logger.getLogger(AnchorPaneMain.class.getName());

    @FXML
    public Label label;
    @FXML public Button select;

    public AnchorPaneMain() {
        System.out.println(WindowUtils.FXML_PATH + "AnchorPaneMain.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(WindowUtils.FXML_PATH + "AnchorPaneMain.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            logger.info("FXML file (AnchorPaneMain) loaded successfully");

            select.setOnAction(event -> onSelectButtonClick());

        } catch (Exception e) {
            e.printStackTrace();
            logger.warning("Error loading FXML file (AnchorPaneMain.fxml): " + e.getMessage());
        }
    }

    public void onSelectButtonClick() {
        logger.info("Select button clicked!");
    }
}
