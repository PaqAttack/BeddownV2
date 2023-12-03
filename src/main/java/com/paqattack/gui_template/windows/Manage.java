package com.paqattack.gui_template.windows;

import com.paqattack.gui_template.Session;
import com.paqattack.gui_template.WindowManager;
import com.paqattack.gui_template.data.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

// TODO ADD BED MANAGEMENT
public class Manage extends AnchorPane implements Updatable {
    WindowManager windowManager;
    @FXML
    ListView<Employee> empList;
    @FXML
    ListView<Bed> bedList;
    @FXML
    Button mngSelectedBtn, importAirmenOld, importAirmen, importBeds, deleteBed, unassignBed;
    @FXML
    Label importAirmenLabel, importAirmenLabelOld, bedLabel;
    @FXML
    CheckBox InUseBeds;
    @FXML
    RadioButton allBed, maleBed, femaleBed, allAssigned, assignedBeds, unassignedBeds;

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

        mngSelectedBtn.setOnMouseClicked(e -> transferAirmen());
        importAirmen.setOnMouseClicked((e -> importAirmen()));
        importAirmenOld.setOnMouseClicked(e-> importOldAirmen());

        allBed.setOnAction(e -> updateBeds());
        maleBed.setOnAction(e -> updateBeds());
        femaleBed.setOnAction(e -> updateBeds());

        allAssigned.setOnMouseClicked(e -> updateBeds());
        assignedBeds.setOnMouseClicked(e -> updateBeds());
        unassignedBeds.setOnMouseClicked(e -> updateBeds());

        InUseBeds.setOnAction(e-> updateBeds());
        unassignBed.setOnAction(e->unassignBed());
        deleteBed.setOnAction(e->deleteBed());
        importBeds.setOnAction(e->importBeds());
    }

    private void importBeds() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.CSV"));
        File file = fileChooser.showOpenDialog(windowManager.getMainStage());

        if (Bed.loadBedFile(file)) {
            WindowUtils.showStatusLabel(bedLabel, "G", "Imported bed file");
        } else {
            WindowUtils.showStatusLabel(bedLabel, "R", "Error loading bed file");
        }
        update();
    }

    private void deleteBed() {
        if (bedList.getSelectionModel().getSelectedItem() != null) {
            if (bedList.getSelectionModel().getSelectedItem().getOccupier() != null) {
                bedList.getSelectionModel().getSelectedItem().getOccupier().unassignBed();
                logger.log(Level.INFO, "Bed {0} unassigned", bedList.getSelectionModel().getSelectedItem().getName());
            }
            Session.getSession().removeBed(bedList.getSelectionModel().getSelectedItem());
            WindowUtils.showStatusLabel(bedLabel, "G", "Bed Deleted");
        } else {
            WindowUtils.showStatusLabel(bedLabel, "R", "No Bed Selected");
            logger.log(Level.INFO, "Bed unassignment attempted but no bed selected.");
        }
        update();
    }


    private void unassignBed() {
        if (bedList.getSelectionModel().getSelectedItem() != null) {
            if (bedList.getSelectionModel().getSelectedItem().getOccupier() != null) {
                bedList.getSelectionModel().getSelectedItem().getOccupier().unassignBed();
                WindowUtils.showStatusLabel(bedLabel, "G", "Bed unassigned");
                logger.log(Level.INFO, "Bed {0} unassigned", bedList.getSelectionModel().getSelectedItem().getName());
            } else {
                WindowUtils.showStatusLabel(bedLabel, "R", "Bed is unoccupied");
                logger.log(Level.INFO, "Bed {0} unassignment attempted but no employee assigned.", bedList.getSelectionModel().getSelectedItem().getName());
            }
        } else {
            WindowUtils.showStatusLabel(bedLabel, "R", "No Bed Selected");
            logger.log(Level.INFO, "Bed unassignment attempted but no bed selected.");
        }
        update();
    }

    public void updateBeds() {
        List<Bed> bedsToShow;

        if (allAssigned.isSelected()) {
            bedsToShow = Session.getSession().getBeds();
        } else if (assignedBeds.isSelected()) {
            bedsToShow = Session.getSession().getAssignedBeds();
        } else if (unassignedBeds.isSelected()) {
            bedsToShow = Session.getSession().getUnassignedBeds();
        } else {
            logger.log(Level.WARNING, "Unable to determine selection assignment mode for bed management");
            bedsToShow = Session.getSession().getBeds();
        }

        if (maleBed.isSelected()) {
            bedsToShow = bedsToShow.stream().filter(e-> e.getGender() == Gender.MALE).collect(Collectors.toList());
        } else if (femaleBed.isSelected()) {
            bedsToShow = bedsToShow.stream().filter(e-> e.getGender() == Gender.FEMALE).collect(Collectors.toList());
        }

        if (InUseBeds.isSelected()) {
            try {
                bedsToShow = bedsToShow.stream().filter(e -> (e.isAssigned() && e.getOccupier().isInBed())).collect(Collectors.toList());
            } catch (Exception exception) {
                logger.log(Level.WARNING, "Error getting occupier isInBed on manage page filtering. {0}", exception.getMessage());
                InUseBeds.setSelected(false);
                InUseBeds.setText("ERROR");
                InUseBeds.setVisible(false);
            }
        }
        WindowUtils.updateObservableListView(bedList, bedsToShow);
    }

    private void transferAirmen() {
        if (empList.getSelectionModel().getSelectedItem() != null) {
            // pass selected employee to the NEW_AIRMEN screen for editing
            windowManager.passEmployeeData(empList.getSelectionModel().getSelectedItem());
            windowManager.selectWindow(WindowManager.BeddownWindow.NEW_AIRMEN);
            windowManager.setWindowLock(true);
        }
        // if no Airmen is selected then do nothing
    }

    @Override
    public void update() {
        // sort by last name
        Platform.runLater(() -> {
            WindowUtils.updateObservableListView(empList, Session.getSession().getEmployees().stream()
                    .sorted(Comparator.comparing(Employee::getName))
                    .toList());
            updateBeds();
        });
    }

    private void importAirmen() {
        // All these filechoosers probably need to be consolidated, possibly in Resource manager.
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Exercise Save Files", "*.DAT"));
        File file = fileChooser.showOpenDialog(windowManager.getMainStage());

        if (Session.getSession().loadFile(file, true)) {
            WindowUtils.showStatusLabel(importAirmenLabel, "G", "Import Success");
        } else {
            WindowUtils.showStatusLabel(importAirmenLabel, "R", "Import Failed");
        }
    }

    private void importOldAirmen() {
        // All these filechoosers probably need to be consolidated, possibly in Resource manager.
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Legacy Exercise Save Files", "*.TXT"));
        File file = fileChooser.showOpenDialog(windowManager.getMainStage());

        if (loadLegacySave(file)) {
            WindowUtils.showStatusLabel(importAirmenLabelOld, "G", "Import Success");
        } else {
            WindowUtils.showStatusLabel(importAirmenLabelOld, "R", "Import Failed");
        }
    }


    /**
     * Load a save file from the October 2023 exercise and add all employees from that save file to this session.
     * This is only relevant to the 25 FGS. This will NOT load files saved from this program as the format is
     * different.
     * @param file The file to be loaded
     * @return boolean representing success or failure.
     */
    private boolean loadLegacySave(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file.toString()))) {
            String line;

            // Stages:
            // 1 = Beds
            // 2 = Personnel
            // 3 = list events

            int stage = 0;

            while ((line = reader.readLine()) != null) {

                if (line.startsWith("[")) {
                    stage = getStage(line);
                    continue;
                }

                switch (stage) {
                    case 1 -> {}
                    case 3 -> {}
                    case 2 -> {
                        String[] person = line.split(";");
                        // id ; rank ; name ; wce ; gender ; bed assigned 0 or designation

                        try {
                            // N style UIDs were not saved properly in the October exercise. There were only 4 people with them so they
                            // are excluded here. Only M style IDs are used.
                            if (!person[0].toUpperCase().startsWith("M")) {
                                logger.log(Level.INFO, "Unsupported UID ({0}) skipped", person[0]);
                                continue;
                            }
                            Rank rank = new Rank(person[1]);
                            Workcenter wce = new Workcenter(person[3]);
                            Gender gender = person[4].toUpperCase().startsWith("F") ? Gender.FEMALE : Gender.MALE;

                            Employee emp = new Employee(person[0], person[2], rank, gender, wce);
                            Session.getSession().addEmployee(emp);
                            // This log event triggers a lot but can be useful for debugging.
                            // logger.log(Level.INFO, "Loaded employee: {0}", emp.getName());
                        } catch (Exception e) {
                            logger.log(Level.WARNING, "Unable to load employee: {0}", person[0]);
                        }
                    }
                    default -> {
                        // This triggering means the format does not match what's expected.
                        logger.log(Level.WARNING, "Unexpected Stage while loading legacy file. See Line {0}", line);
                        return false;
                    }
                }
            }

        } catch (IOException e) {
            logger.log(Level.WARNING, "I/O Error loading legacy file.");
            return false;
        }
        update();
        return true;
    }

    /**
     * Helper function for loading files.
     * @param header line being processed. [XYZ] expected.
     * @return int representing stage. 1=Bed, 2=Prsnl 3=Events
     */
    private int getStage(String header) {
        // Stages:
        // 1 = Beds
        // 2 = Personnel
        // 3 = list events

        if (header.equalsIgnoreCase("[PSNL]")) {
            return 2;
        } else if (header.equalsIgnoreCase("[BEDS]")) {
            return 1;
        } else if (header.equalsIgnoreCase("[LSTE]")) {
            return 3;
        } else {
            return -1;
        }
    }


}
