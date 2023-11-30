package com.paqattack.gui_template.windows;

import com.paqattack.gui_template.WindowManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainWindow extends BorderPane implements Updatable  {
    WindowManager windowManager;
    private static final Logger logger = Logger.getLogger(MainWindow.class.getName());

    public MainWindow(WindowManager windowManager) {
        this.windowManager = windowManager;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(WindowUtils.FXML_PATH + "MainWindow.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            logger.log(Level.INFO, "FXML file loaded successfully from {0}", getClass().getName());

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error loading FXML file from {0}", getClass().getName());
        }
    }

    @Override
    public void update() {
        // Required interface for scenes but not required for the background one as it merely contains other
        // scenes
    }
}
