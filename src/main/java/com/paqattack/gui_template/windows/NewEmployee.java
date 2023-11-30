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
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class NewEmployee extends AnchorPane implements Updatable {
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

    private final ArrayList<String> genders = new ArrayList<>(2);

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

        setNoneBtn.setOnMouseClicked(e -> assignBedChoice.getSelectionModel().select(0));

        setRandomBtn.setOnMouseClicked(e -> {
            try {
                Random rand = new Random();
                int min = 1; // Set the minimum value of the range
                int max = assignBedChoice.getItems().size(); // Set the maximum value of the range
                assignBedChoice.getSelectionModel().select(rand.nextInt(max - min) + min);
            } catch (Exception exception) {
                assignBedChoice.getSelectionModel().select(0);
                logger.log(Level.WARNING, "Random selection of bed to assign failed. Exception: {0}", exception.getMessage());
            }
        });
    }

    private void saveNewEmployee() {
        String name = nameTxt.getText();
        String uid = uidTxt.getText();
        Rank rank = new Rank(rankChoice.getValue());
        Workcenter workcenter = new Workcenter(workcenterChoice.getValue());
        Gender gender = genderChoice.getValue().toUpperCase().startsWith("F") ? Gender.FEMALE : Gender.MALE;
        String bedName = assignBedChoice.getValue();

        Bed bed = null;
        if (!bedName.equalsIgnoreCase("NONE")) {
            bed = Session.getSession().getBedByName(bedName);
        }

        if (scannedData != null) {  // if you are processing a newly scanned ID
            Employee emp;
            if (bed == null) {
                emp = Session.getSession().addAndGetEmployee(new Employee(uid, name, rank, gender, workcenter));
            } else {
                emp = Session.getSession().addAndGetEmployee(new Employee(uid, name, rank, gender, workcenter, bed));
            }

            // set newly scanned employees to be in the building by default to ensure they dont need to scan again.
            emp.setInside(true);
            Session.getSession().addEntry(new ListEntry(emp, new DateTime(), true, true, false, false));
            scannedData = null;
        }

        if (employeeBeingUpdated != null) {  // If you are processing an existing employee.

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

    // leave the page without saving progress
    public void exitWithoutSave() {
        scannedData = null;
        employeeBeingUpdated = null;
        windowManager.setWindowLock(false);
        windowManager.selectWindow(WindowManager.BeddownWindow.CHECKINOUT);
        logger.log(Level.INFO, "Exiting without saving employee changes");
    }

    // called by window manager
    public void setScannedData(ScannedData scannedData) {
        this.scannedData = scannedData;
        employeeBeingUpdated = null;
        logger.log(Level.INFO, "New employee {0} scanned to be updated", scannedData.getLast());
    }

    // called by window manager
    public void setEmployeeBeingUpdated(Employee emp) {
        this.employeeBeingUpdated = emp;
        scannedData = null;
        logger.log(Level.INFO, "Employee {0} selected to be updated", emp);
    }

    private void addWorkcenter() {
        String workcenterName = workcenterAddTxt.getText();

        // if workcenter is blank then do nothing
        if (workcenterName.isBlank()) {
            logger.log(Level.WARNING, "Workcenter name is blank and cant be added.");
            return;
        }
        new Workcenter(workcenterName);
        updateWorkcenter();
        workcenterChoice.setValue(workcenterName);
        workcenterAddTxt.clear();
    }

    @Override
    public void update() {
        Platform.runLater(() -> {
            updateElements();

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
        updateRanks();
        updateGender();
        updateWorkcenter();
        updateBeds();
        logger.log(Level.INFO, "NewEmployee Window updated");
    }

    /**
     * Update beds to only show unassigned beds matching provided gender.
     */
    private void updateBeds() {
        if (genderChoice.getValue() != null && genderChoice.getValue().startsWith("M")) { // if male is selected as gender then show only male unassigned beds
            List<String> unassignedMaleBeds = new ArrayList<>(Session.getSession().getUnassignedMaleBeds().stream()
                    .map(Bed::getName)
                    .toList());

            // add a NONE selection option
            unassignedMaleBeds.add(0, "NONE");

            // add current bed to list at element 1 so it can be selected by default.
            if (employeeBeingUpdated != null && employeeBeingUpdated.getBed() != null) {
                unassignedMaleBeds.add(1, employeeBeingUpdated.getBed().getName());
            }

            assignBedChoice.setItems(FXCollections.observableArrayList(unassignedMaleBeds));

        } else if (genderChoice.getValue() != null && genderChoice.getValue().startsWith("F")) { // if female is selected as gender then show only male unassigned beds

            List<String> unassignedFemaleBeds = new ArrayList<>(Session.getSession().getUnassignedFemaleBeds().stream()
                    .map(Bed::getName)
                    .toList());

            // add a NONE selection option
            unassignedFemaleBeds.add(0, "NONE");

            // add current bed to list at element 1 so it can be selected by default.
            if (employeeBeingUpdated != null && employeeBeingUpdated.getBed() != null) {
                unassignedFemaleBeds.add(1, employeeBeingUpdated.getBed().getName());
            }

            assignBedChoice.setItems(FXCollections.observableArrayList(unassignedFemaleBeds));
        } else {
            // This should never happen
            logger.log(Level.WARNING, "No gender identified when creating list of available beds");

            List<String> unassignedBeds = new ArrayList<>(Session.getSession().getUnassignedBeds().stream()
                    .map(Bed::getName)
                    .toList());
            unassignedBeds.add(0, "NONE");

            if (employeeBeingUpdated != null && employeeBeingUpdated.getBed() != null) {
                unassignedBeds.add(1, employeeBeingUpdated.getBed().getName());
            }

            assignBedChoice.setItems(FXCollections.observableArrayList(unassignedBeds));
        }
        // set default as first element.
        assignBedChoice.setValue(assignBedChoice.getItems().get(0));
    }

    private void updateWorkcenter() {
        workcenterChoice.setItems(FXCollections.observableArrayList(Workcenter.getWorkcenterNames()));
    }

    private void updateGender() {
        genderChoice.setItems(FXCollections.observableArrayList(genders));
        genderChoice.setValue("Male");
    }

    private void updateRanks() {
        rankChoice.setItems(FXCollections.observableArrayList(Rank.getAbbrevRanks()));
    }
}
