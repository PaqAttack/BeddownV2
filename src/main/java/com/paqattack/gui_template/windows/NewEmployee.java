package com.paqattack.gui_template.windows;

import com.paqattack.gui_template.Session;
import com.paqattack.gui_template.WindowManager;
import com.paqattack.gui_template.data.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NewEmployee extends AnchorPane implements Updatable  {
    WindowManager windowManager;
    private ScannedData scannedData;

    @FXML
    TextField nameTxt;
    @FXML
    TextField workcenterAddTxt;
    @FXML
    TextField uidTxt;
    @FXML
    ChoiceBox<String> rankChoice;
    @FXML
    ChoiceBox<String> genderChoice;
    @FXML
    ChoiceBox<String> workcenterChoice;
    @FXML
    ChoiceBox<Bed> assignBedChoice;
    @FXML
    Button addWorkcenterBtn;
    @FXML
    Button setNoneBtn;
    @FXML
    Button setRandomBtn;
    @FXML
    Button doneBtn;
    @FXML
    Button exitwoSaveBtn;

    private ArrayList<String> genders = new ArrayList<>(2);

    private static final Logger logger = Logger.getLogger(NewEmployee.class.getName());

    public NewEmployee(WindowManager windowManager) {
        this.windowManager = windowManager;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(WindowUtils.FXML_PATH + "NewEmployee.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            logger.log(Level.INFO, "FXML file loaded successfully from {0}", getClass().getName());

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error loading FXML file from {0}", getClass().getName());
        }

        genders.add("Male");
        genders.add("Female");

        genderChoice.setOnAction(e -> updateBeds());
        addWorkcenterBtn.setOnMouseClicked(e -> addWorkcenter());
    }

    public ScannedData getScannedData() {
        return scannedData;
    }

    public void setScannedData(ScannedData scannedData) {
        this.scannedData = scannedData;
    }

    private void addWorkcenter() {
        String workcenterName = workcenterAddTxt.getText();
        if (workcenterName.isBlank()) {
            logger.log(Level.WARNING, "Workcenter name is blank");
            return;
        }
        new Workcenter(workcenterName);
        updateWorkcenter();
        workcenterChoice.setValue(workcenterName);
        workcenterAddTxt.clear();
    }

    @Override
    public void update() {
        updateElements();
        if (scannedData != null) {
            nameTxt.setText(scannedData.getLast() + ", " + scannedData.getFirst());
            uidTxt.setText(scannedData.getId());
            rankChoice.setValue(scannedData.getRank());
        }
    }

    private void updateElements() {
        Platform.runLater(() -> {
            updateRanks();
            updateGender();
            updateWorkcenter();
            updateBeds();
            logger.log(Level.INFO, "NewEmployee Window updated");
        });
    }

    private void updateBeds() {
        if (genderChoice.getValue().startsWith("M")) {
            assignBedChoice.setItems(FXCollections.observableArrayList(Session.getSession().getUnassignedMaleBeds()));
        } else if (genderChoice.getValue().startsWith("F")) {
            assignBedChoice.setItems(FXCollections.observableArrayList(Session.getSession().getUnassignedFemaleBeds()));
        } else {
            assignBedChoice.setItems(FXCollections.observableArrayList(Session.getSession().getUnassignedBeds()));
        }
        assignBedChoice.setValue(assignBedChoice.getItems().get(0));
    }

    private void updateWorkcenter() {
        workcenterChoice.setItems(FXCollections.observableArrayList(Workcenter.getWorkcenterNames()));
        workcenterChoice.setValue("Unknown");
    }

    private void updateGender() {
        genderChoice.setItems(FXCollections.observableArrayList(genders));
        genderChoice.setValue("Male");
    }

    private void updateRanks() {
        rankChoice.setItems(FXCollections.observableArrayList(Rank.getFullRanks()));
        rankChoice.setValue("Amn");
    }
}
