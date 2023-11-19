package com.paqattack.gui_template.windows;

import com.paqattack.gui_template.Session;
import com.paqattack.gui_template.WindowManager;
import com.paqattack.gui_template.data.Employee;
import com.paqattack.gui_template.data.Workcenter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Status extends AnchorPane implements Updatable  {
    WindowManager windowManager;
    @FXML
    Label amnCheckedInLabel;
    @FXML
    Label beddownCheckedIn;
    @FXML
    Label availBeds;
    @FXML
    Label assignedBeds;
    @FXML
    Label totalBeds;
    @FXML
    Button locationReportBtn;
    @FXML
    Button bedReporBtn;
    @FXML
    ListView<String> locationListView;
    final String newline = System.getProperty("line.separator");
    private static final Logger logger = Logger.getLogger(Status.class.getName());

    public Status(WindowManager windowManager) {
        this.windowManager = windowManager;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(WindowUtils.FXML_PATH + "Status.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            logger.log(Level.INFO, "FXML file loaded successfully from {0}", getClass().getName());

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error loading FXML file from {0}", getClass().getName());
        }

        bedReporBtn.setOnAction(event -> generateBeddownReport());
        locationReportBtn.setOnAction(event -> generateBldgReport());
    }

    public void updateLabels() {
        // Airmen Checked in Location
        amnCheckedInLabel.setText(String.valueOf(Session.getSession().getBldgCheckedInEmployeeNames().size()));

        // Beddown Checked in Location
        beddownCheckedIn.setText(String.valueOf(Session.getSession().getBeddownCheckedInEmployees().size()));

        // Available Beds
        availBeds.setText(String.valueOf(Session.getSession().getUnassignedBeds().size()));

        // Assigned Beds
        assignedBeds.setText(String.valueOf(Session.getSession().getAssignedBeds().size()));

        // Total Beds
        totalBeds.setText(String.valueOf(Session.getSession().getBeds().size()));
    }

    @Override
    public void update() {
        Platform.runLater(() -> {
            updateLabels();
            WindowUtils.updateObservableListView(locationListView, Session.getSession().getBldgCheckedInEmployeeNames());
            logger.log(Level.INFO, "Status window updated");
        });
    }

    public void generateBeddownReport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.TXT"));
        fileChooser.setTitle("Save Beddown Report");
        File file = fileChooser.showSaveDialog(windowManager.getMainStage());

        saveBeddownFile(file);
    }

    private void saveBeddownFile(File newFile) {
        List<Employee> emps = Session.getSession().getBeddownCheckedInEmployees();

        try (FileWriter fw = new FileWriter(newFile.getAbsoluteFile());
             BufferedWriter bw = new BufferedWriter(fw)) {

            // write header & beds
            bw.append("BED DOWN REPORT").append(newline).append(newline);
            bw.append("Airmen Currently Bedding down Here: ").append(String.valueOf(Session.getSession().getBeddownCheckedInEmployees().size())).append(newline);
            bw.append("Currently Available Beds: ").append(String.valueOf(Session.getSession().getUnassignedBeds().size())).append(newline);
            bw.append("Currently Assigned Beds: ").append(String.valueOf(Session.getSession().getAssignedBeds().size())).append(newline);
            bw.append("Total beds at facility: ").append(String.valueOf(Session.getSession().getBeds().size())).append(newline).append(newline);


            for (String section : Workcenter.getWorkcenterNames()) {
                bw.append(section.toUpperCase()).append(":").append(newline);
                for (Employee emp : emps) {
                    if (emp.getWorkcenter().getName().equalsIgnoreCase(section)) {
                        String currentBed = emp.getName(25) + " " + emp.getBed().getName() + newline;
                        bw.append(currentBed);
                    }
                }
                bw.append(newline);
            }

            bw.append("Unknown Workcenter:").append(newline);
            for (Employee emp : emps) {
                if (emp.getWorkcenter().getName().equalsIgnoreCase("")) {
                    String currentBed = emp.getName(25) + " " + emp.getBed().getName() + newline;
                    bw.append(currentBed);
                }
            }

            logger.log(Level.INFO, "Beddown report saved successfully to {0}", newFile.getName());
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error saving bed file");
        }
        openSaveFile(newFile.getAbsolutePath());
    }

    public void generateBldgReport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.TXT"));
        fileChooser.setTitle("Save Building Report");
        File file = fileChooser.showSaveDialog(windowManager.getMainStage());

        saveBldgFile(file);
    }

    private void saveBldgFile(File newFile) {
        List<Employee> emps = Session.getSession().getBldgCheckedInEmployees();

        try (FileWriter fw = new FileWriter(newFile.getAbsoluteFile());
             BufferedWriter bw = new BufferedWriter(fw)) {

            // write header & beds
            bw.append("BUILDING ACCOUNTABILITY REPORT").append(newline).append(newline);
            bw.append("Airmen Currently Checked In: ").append(String.valueOf(emps.size())).append(newline);

            for (String section : Workcenter.getWorkcenterNames()) {
                bw.append(section.toUpperCase()).append(":").append(newline);
                for (Employee emp : emps) {
                    if (emp.getWorkcenter().getName().equalsIgnoreCase(section)) {
                        String currentBed = emp + newline;
                        bw.append(currentBed);
                    }
                }
                bw.append(newline);
            }

            bw.append("Unknown Workcenter:").append(newline);
            for (Employee emp : emps) {
                if (emp.getWorkcenter().getName().equalsIgnoreCase("")) {
                    String currentBed = emp + newline;
                    bw.append(currentBed);
                }
            }

            logger.log(Level.INFO, "Building report saved successfully to {0}", newFile.getName());
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error saving building file");
        }

        openSaveFile(newFile.getAbsolutePath());
    }

    private void openSaveFile(String path) {
        if (!Desktop.isDesktopSupported()) {
            logger.log(Level.WARNING, "Desktop is not supported (fatal)");
            return;
        }

        Desktop desktop = Desktop.getDesktop();
        if (!desktop.isSupported(Desktop.Action.OPEN)) {
            logger.log(Level.WARNING, "Desktop is not supported (fatal)");
            return;
        }

        try {
            File file = new File(path);
            logger.log(Level.INFO, "Opening save file: {0}", file.getAbsolutePath());
            desktop.open(file);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error opening help file: {0}", e.getMessage());
        }
    }
}
