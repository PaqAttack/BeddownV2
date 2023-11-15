package com.example.updated_beddown.windows;

import com.example.updated_beddown.TMAAAReader.MyReader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.util.logging.Logger;

public class AnchorPaneMain  extends AnchorPane {

    private static final Logger logger = Logger.getLogger(AnchorPaneMain.class.getName());

    @FXML
    public Label label;
    @FXML public Button select;

    public AnchorPaneMain() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(WindowUtils.FXML_PATH + "AnchorPaneMain.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            logger.info("FXML file (AnchorPaneMain) loaded successfully");

            select.setOnAction(event -> onSelectButtonClick());

        } catch (Exception e) {
            logger.warning("Error loading FXML file (AnchorPaneMain.fxml): " + e.getMessage());
        }
    }

    public void onSelectButtonClick() {
        label.setText("Building TMAAA Reports... Please Wait.");
        String absoluteFileName = WindowUtils.saveFilePicker(this.getScene().getWindow(), null, "Select 620 File from IMDS");

        if (absoluteFileName == null) {
            label.setText("No file selected.");
            return;
        }

        try {
            MyReader myReader = new MyReader(absoluteFileName);
        } catch (Exception e) {
            logger.warning("Error reading file: " + e.getMessage());
            label.setText("Error reading file");
            return;
        }

        label.setText("Generated TMAAA Reports.");
        logger.info("Select button clicked!");
    }
}
