package com.paqattack.gui_template.windows;

import com.paqattack.gui_template.WindowManager;
import com.paqattack.gui_template.data.Bed;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StartUp extends AnchorPane implements Updatable  {
    WindowManager windowManager;
    @FXML Button helpCreateBedBtn;
    @FXML
    Button saveSessionBtn;
    @FXML
    Button loadSaveBtn;
    @FXML
    Button loadBedBtn;
    @FXML
    Label feedbackLabel;
    @FXML
    Circle helpBedImg;
    @FXML
    Circle saveSsnImg;
    @FXML
    Circle loadSaveImg;
    @FXML
    Circle loadBedImg;
    private static final Logger logger = Logger.getLogger(StartUp.class.getName());

    public StartUp(WindowManager windowManager) {
        this.windowManager = windowManager;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(WindowUtils.FXML_PATH + "StartUp.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            logger.log(Level.INFO, "FXML file loaded successfully from {0}", getClass().getName());

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error loading FXML file from {0}", getClass().getName());
        }

        setCicleVisibility();

        helpCreateBedBtn.setOnAction(event -> helpCreateBed());
        saveSessionBtn.setOnAction(event -> saveSession());
        loadSaveBtn.setOnAction(event -> loadSaveFile());
        loadBedBtn.setOnAction(event -> loadBedFile());

        helpCreateBedBtn.setOnMouseEntered(event -> helpBedImg.setVisible(true));
        saveSessionBtn.setOnMouseEntered(event -> saveSsnImg.setVisible(true));
        loadSaveBtn.setOnMouseEntered(event -> loadSaveImg.setVisible(true));
        loadBedBtn.setOnMouseEntered(event -> loadBedImg.setVisible(true));

        helpCreateBedBtn.setOnMouseExited(event -> helpBedImg.setVisible(false));
        saveSessionBtn.setOnMouseExited(event -> saveSsnImg.setVisible(false));
        loadSaveBtn.setOnMouseExited(event -> loadSaveImg.setVisible(false));
        loadBedBtn.setOnMouseExited(event -> loadBedImg.setVisible(false));
    }

    private void setCicleVisibility() {
        loadSaveImg.setVisible(false);
        loadBedImg.setVisible(false);
        saveSsnImg.setVisible(false);
        helpBedImg.setVisible(false);
    }

    private void loadBedFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.CSV"));
        File file = fileChooser.showOpenDialog(windowManager.getMainStage());

        if (Bed.loadBedFile(file)) {
            windowManager.setWindowLock(false);
            windowManager.selectWindow(WindowManager.BeddownWindow.CHECKINOUT);
        } else {
            WindowUtils.showStatusLabel(feedbackLabel, "R", "Error loading bed file");
        }
    }

    private void loadSaveFile() {
        if (windowManager.getSession().loadSaveFile()) {
            windowManager.setWindowLock(false);
            windowManager.selectWindow(WindowManager.BeddownWindow.CHECKINOUT);
            logger.log(Level.INFO, "Save file loaded successfully");
        } else {
            WindowUtils.showStatusLabel(feedbackLabel, "R", "Error loading save file");
            logger.log(Level.WARNING, "Error loading save file");
        }
    }

    private void saveSession() {
        if (windowManager.getSession().saveSessionFile()) {
            WindowUtils.showStatusLabel(feedbackLabel, "G", "Exercise file saved successfully");
            logger.log(Level.INFO, "Exercise file saved successfully");
        } else {
            WindowUtils.showStatusLabel(feedbackLabel, "R", "Error loading save file");
            logger.log(Level.WARNING, "Error loading save file");
        }
    }

    private void helpCreateBed() {
        if (!Desktop.isDesktopSupported()) {
            logger.log(Level.WARNING, "Desktop is not supported (fatal)");
            WindowUtils.showStatusLabel(feedbackLabel, "R", "Powerpoint unsupported on this device");
            return;
        }

        Desktop desktop = Desktop.getDesktop();
        if (!desktop.isSupported(Desktop.Action.OPEN)) {
            logger.log(Level.WARNING, "Desktop is not supported (fatal)");
            WindowUtils.showStatusLabel(feedbackLabel, "R", "Powerpoint unsupported on this device");
            return;
        }

        try {
            windowManager.getResourceManager().extractResource("help/BedFileHelper.pptx");
            File file = new File(windowManager.getResourceManager().getResourceAbsoluteFilePath("help/BedFileHelper.pptx"));
            logger.log(Level.INFO, "Opening help file: {0}", file.getAbsolutePath());
            WindowUtils.showStatusLabel(feedbackLabel, "G", "Powerpoint help file opening");
            desktop.open(file);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error opening help file: {0}", e.getMessage());
            WindowUtils.showStatusLabel(feedbackLabel, "R", "Unexpected Powerpoint error");
        }
    }

    @Override
    public void update() {
        // Required interface for scenes but not required for this one
    }
}
