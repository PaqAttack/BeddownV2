package com.paqattack.gui_template.windows;

import com.paqattack.gui_template.WindowManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Sidebar extends VBox implements Updatable  {
    private static final Logger logger = Logger.getLogger(Sidebar.class.getName());
    WindowManager windowManager;

    @FXML Button startUpBtn;
    @FXML
    private Button manageBtn;
    @FXML
    private Button checkBtn;
    @FXML
    private Button statusBtn;
    @FXML
    private Button reportBtn;

    public Sidebar(WindowManager windowManager) {
        this.windowManager = windowManager;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(WindowUtils.FXML_PATH + "Sidebar.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            logger.log(Level.INFO, "FXML file loaded successfully from {0}", getClass().getName());

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error loading FXML file from {0}", getClass().getName());
        }

        startUpBtn.setOnAction(event -> windowManager.selectWindow(WindowManager.BeddownWindow.STARTUP));
        manageBtn.setOnAction(event -> windowManager.selectWindow(WindowManager.BeddownWindow.MANAGE));
        checkBtn.setOnAction(event -> windowManager.selectWindow(WindowManager.BeddownWindow.CHECKINOUT));
        statusBtn.setOnAction(event -> windowManager.selectWindow(WindowManager.BeddownWindow.STATUS));
    }

    @Override
    public void update() {
        // Required interface for scenes but not required for this one
    }
}
