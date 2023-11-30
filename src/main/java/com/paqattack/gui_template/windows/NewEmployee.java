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
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

//TODO ADD BED NONE/RANDOM OPTIONS
public class NewEmployee extends AnchorPane implements Updatable  {
    WindowManager windowManager;
    private ScannedData scannedData;
    private Employee employeeBeingUpdated;

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
    ChoiceBox<String> assignBedChoice;
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
        doneBtn.setOnMouseClicked(e -> saveNewEmployee());
        exitwoSaveBtn.setOnMouseClicked(e -> exitWithoutSave());
    }

    private void saveNewEmployee() {
        String name = nameTxt.getText();
        String uid = uidTxt.getText();
        Rank rank = new Rank(rankChoice.getValue());
        Workcenter workcenter = new Workcenter(workcenterChoice.getValue());
        Gender gender = genderChoice.getValue().toUpperCase().startsWith("F")? Gender.FEMALE : Gender.MALE;

        String bedName = assignBedChoice.getValue();
        Bed bed = null;
        if (!bedName.equalsIgnoreCase("NONE")) {
            bed = Session.getSession().getBedByName(bedName);
        }

        if (scannedData != null) {
            Employee emp;
            if (bed == null) {
                emp = Session.getSession().addAndGetEmployee(new Employee(uid, name, rank, gender, workcenter));
            } else {
                emp = Session.getSession().addAndGetEmployee(new Employee(uid, name, rank, gender, workcenter, bed));
            }
            emp.setInside(true);
            Session.getSession().addEntry(new ListEntry(emp, new DateTime(), true, true, false, false));
            scannedData = null;
        }

        if (employeeBeingUpdated != null) {

            if (bed == null) {
                if (employeeBeingUpdated.getBed() != null) {        // setting a person with a bed to no bed
                    employeeBeingUpdated.unassignBed();
                }
            } else {   // If new bed selected....
                if (employeeBeingUpdated.getBed() != null) { // unassign any previous bed
                    employeeBeingUpdated.unassignBed();
                }
                employeeBeingUpdated.setBed(bed); // assign new bed
            }

            employeeBeingUpdated.setGender(gender);
            employeeBeingUpdated.setName(name);
            employeeBeingUpdated.setRank(rank);
            employeeBeingUpdated.setWorkcenter(workcenter);
            employeeBeingUpdated = null;
        }

        windowManager.setWindowLock(false);
        windowManager.selectWindow(WindowManager.BeddownWindow.CHECKINOUT);
    }

    public void exitWithoutSave() {
        scannedData = null;
        windowManager.setWindowLock(false);
        windowManager.selectWindow(WindowManager.BeddownWindow.CHECKINOUT);
        logger.log(Level.INFO, "Exiting without saving employee changes");
    }

    public void setScannedData(ScannedData scannedData) {
        this.scannedData = scannedData;
        employeeBeingUpdated = null;
        logger.log(Level.INFO, "New employee {0} scanned to be updated", scannedData.getLast());
    }

    public void setEmployeeBeingUpdated(Employee emp) {
        this.employeeBeingUpdated = emp;
        scannedData = null;
        logger.log(Level.INFO, "Employee {0} selected to be updated", emp);
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
        Platform.runLater(() -> {
            if (scannedData != null) {   // If adding newly scanned employee
                nameTxt.setText(scannedData.getLast() + ", " + scannedData.getFirst());
                uidTxt.setText(scannedData.getId());
                rankChoice.setValue(scannedData.getRank());
                return;
            }
            if (employeeBeingUpdated != null) {  // if managed existing employee
                nameTxt.setText(employeeBeingUpdated.getName());
                uidTxt.setText(employeeBeingUpdated.getUID());
                rankChoice.setValue(employeeBeingUpdated.getRank().getAbbreviation());

                if (employeeBeingUpdated.getBed() == null) {
                    assignBedChoice.setValue("NONE");
                } else {
                    assignBedChoice.setValue(employeeBeingUpdated.getBed().getName());
                }

                workcenterChoice.setValue(employeeBeingUpdated.getWorkcenter().getName());
            }
        });
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
        if (genderChoice.getValue() != null && genderChoice.getValue().startsWith("M")) {
            List<String> unassignedMaleBeds = new ArrayList<>(Session.getSession().getUnassignedMaleBeds().stream()
                    .map(Bed::getName)
                    .toList());
            unassignedMaleBeds.add(0, "NONE");

            if (employeeBeingUpdated != null && employeeBeingUpdated.getBed() != null) {
                unassignedMaleBeds.add(1, employeeBeingUpdated.getBed().getName());
            }

            assignBedChoice.setItems(FXCollections.observableArrayList(unassignedMaleBeds));

        } else if (genderChoice.getValue() != null && genderChoice.getValue().startsWith("F")) {

            List<String> unassignedFemaleBeds = new ArrayList<>(Session.getSession().getUnassignedFemaleBeds().stream()
                    .map(Bed::getName)
                    .toList());
            unassignedFemaleBeds.add(0, "NONE");

            if (employeeBeingUpdated != null && employeeBeingUpdated.getBed() != null) {
                unassignedFemaleBeds.add(1, employeeBeingUpdated.getBed().getName());
            }

            assignBedChoice.setItems(FXCollections.observableArrayList(unassignedFemaleBeds));
        } else {

            List<String> unassignedBeds = new ArrayList<>(Session.getSession().getUnassignedBeds().stream()
                    .map(Bed::getName)
                    .toList());
            unassignedBeds.add(0, "NONE");

            if (employeeBeingUpdated != null && employeeBeingUpdated.getBed() != null) {
                unassignedBeds.add(1, employeeBeingUpdated.getBed().getName());
            }

            assignBedChoice.setItems(FXCollections.observableArrayList(unassignedBeds));
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
        rankChoice.setItems(FXCollections.observableArrayList(Rank.getAbbrevRanks()));
        rankChoice.setValue("Amn");
    }
}
