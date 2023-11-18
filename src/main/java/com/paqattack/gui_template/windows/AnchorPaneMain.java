package com.paqattack.gui_template.windows;

import com.paqattack.gui_template.ResourceManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

            select.setOnAction(event -> {
                try {
                    onSelectButtonClick();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        } catch (Exception e) {
            logger.warning("Error loading FXML file (AnchorPaneMain.fxml): " + e.getMessage());
        }
    }

    public void onSelectButtonClick() throws IOException {
        ResourceManager rm = new ResourceManager(String.format("%s%s", System.getProperty("user.home"), "/Desktop/FileTesting/"), "/");
        BufferedReader br = new BufferedReader(new InputStreamReader(rm.getInputStreamResource("test.txt")));

        System.out.println(br.readLine());
        logger.info("Select button clicked!");
    }
}
