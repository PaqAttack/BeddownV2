package com.paqattack.gui_template.windows;

import com.paqattack.gui_template.Session;
import com.paqattack.gui_template.WindowManager;
import com.paqattack.gui_template.data.Employee;
import com.paqattack.gui_template.data.Gender;
import com.paqattack.gui_template.data.Rank;
import com.paqattack.gui_template.data.Workcenter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Manage extends AnchorPane implements Updatable {
    WindowManager windowManager;
    @FXML
    ListView<Employee> empList;
    @FXML
    Button mngSelectedBtn, importAirmenOld, importAirmen;
    @FXML
    Label importAirmenLabel, importAirmenLabelOld;

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
        Platform.runLater(() -> WindowUtils.updateObservableListView(empList, Session.getSession().getEmployees().stream()
                .sorted(Comparator.comparing(Employee::getName))
                .toList()));
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
