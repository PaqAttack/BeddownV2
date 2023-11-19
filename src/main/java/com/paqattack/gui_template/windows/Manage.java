package com.paqattack.gui_template.windows;

import com.paqattack.gui_template.WindowManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Manage extends AnchorPane implements Updatable  {
    WindowManager windowManager;
    private static final Logger logger = Logger.getLogger(Manage.class.getName());

    public Manage(WindowManager windowManager) {
        this.windowManager = windowManager;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(WindowUtils.FXML_PATH + "Manage.fxml"));
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
        // TODO document why this method is empty
    }
}
