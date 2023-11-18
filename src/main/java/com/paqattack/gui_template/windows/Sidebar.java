package com.paqattack.gui_template.windows;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.logging.Logger;

public class Sidebar extends VBox {
    private static final Logger logger = Logger.getLogger(Sidebar.class.getName());

    @FXML
    public Label label1;
    @FXML
    public Label label2;
    @FXML
    public Label label3;
    @FXML
    public Label label4;

    public Sidebar() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(WindowUtils.FXML_PATH + "Sidebar.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            logger.info("FXML file (Sidebar) loaded successfully");
        } catch (Exception e) {
            logger.warning("Error loading FXML file (Sidebar.fxml): " + e.getMessage());
        }
    }

    public void setLabelText(String msg) {
        label1.setText(msg);
        label2.setText(msg);
        label3.setText(msg);
        label4.setText(msg);
    }

}
