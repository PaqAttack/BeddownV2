package com.paqattack.gui_template.windows;

import com.paqattack.gui_template.WindowManager;
import com.paqattack.gui_template.data.ScannedData;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.util.logging.Level;
import java.util.logging.Logger;

public class NewEmployee extends AnchorPane implements Updatable  {
    WindowManager windowManager;
    private ScannedData scannedData;
    private static final Logger logger = Logger.getLogger(NewEmployee.class.getName());

    public NewEmployee(WindowManager windowManager) {
        this.windowManager = windowManager;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(WindowUtils.FXML_PATH + "NewPlayer.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            logger.log(Level.INFO, "FXML file loaded successfully from {0}", getClass().getName());

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error loading FXML file from {0}", getClass().getName());
        }
    }

    public ScannedData getScannedData() {
        return scannedData;
    }

    public void setScannedData(ScannedData scannedData) {
        this.scannedData = scannedData;
    }

    @Override
    public void update() {
        // TODO document why this method is empty
    }
}
