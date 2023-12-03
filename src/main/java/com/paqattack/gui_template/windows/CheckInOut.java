package com.paqattack.gui_template.windows;

import com.paqattack.gui_template.ResourceManager;
import com.paqattack.gui_template.Session;
import com.paqattack.gui_template.WindowManager;
import com.paqattack.gui_template.data.Employee;
import com.paqattack.gui_template.data.ListEntry;
import com.paqattack.gui_template.data.ScannedData;
import com.paqattack.gui_template.data.Workcenter;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.joda.time.DateTime;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CheckInOut extends AnchorPane implements Updatable {
    WindowManager windowManager;
    private static final Logger logger = Logger.getLogger(CheckInOut.class.getName());
    @FXML
    TextField scanBox;
    @FXML
    AnchorPane backPane;
    @FXML
    Button clearScanBtn, genReport;
    @FXML
    ListView<ListEntry> entryListView;
    @FXML
    CheckBox bedCheck;
    @FXML
    CheckBox bldgChk;
    final String newline = System.getProperty("line.separator");

    /**
     * When text is entered into the scan test box a timer is started and each character added, restarts the timer.
     * This pause variable is the length of the timer. When the timer ends the scanned text is processed. If this is run on
     * a very slow computer that scans data from buffer very slow this can be increased.
     */
    private PauseTransition pause = new PauseTransition(Duration.seconds(0.8));

    public CheckInOut(WindowManager windowManager) {
        this.windowManager = windowManager;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(WindowUtils.FXML_PATH + "CheckInOut.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
            logger.log(Level.INFO, "FXML file loaded successfully from {0}", getClass().getName());

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error loading FXML file from {0}", getClass().getName());
        }

        // keep resetting the focus to the scan box to minimize frustration of needing to reselect it.
        backPane.setOnMouseMoved(event -> scanBox.requestFocus());
        clearScanBtn.setOnAction(event -> scanBox.clear());
        pause.setOnFinished(event -> processScan());
        scanBox.setOnKeyTyped(event -> extendTimer());

        genReport.setOnAction(e -> generateReport());

        // Ensure that you can never have both buttons deselected.
        bedCheck.setOnAction(event -> {
            if (!bedCheck.isSelected()) {
                bldgChk.setSelected(true);
            }
        });
    }

    public void generateReport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.TXT"));
        fileChooser.setTitle("Save Events Report");
        File file = fileChooser.showSaveDialog(windowManager.getMainStage());

        saveEventnFile(file);
    }

    private void saveEventnFile(File file) {
        List<Employee> emps = Session.getSession().getBeddownCheckedInEmployees();

        try (FileWriter fw = new FileWriter(file.getAbsoluteFile());
             BufferedWriter bw = new BufferedWriter(fw)) {

            // write header & beds
            bw.append("CHECK IN/OUT REPORT").append(newline).append(newline);

            for (int i = 0; i < Session.getSession().getEntries().size(); i++) {
                bw.append(Session.getSession().getEntries().get(i).toString()).append(newline);
            }

            logger.log(Level.INFO, "Beddown report saved successfully to {0}", file.getName());
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error saving bed file");
        }
        ResourceManager.openSaveFile(file.getAbsolutePath());
    }


    private void extendTimer() {
        pause.stop();
        pause.playFromStart();
    }

    /**
     * Process scan from scan box.
     */
    private void processScan() {
        String scannedText = scanBox.getText();

        // Barcode M and N styles are the only ones supported.
        if (scannedText.length() > 80 && (scannedText.startsWith("M") || scannedText.startsWith("N"))) {
            String id = null;
            String first = null;
            String last = null;
            String rank = null;

            if (scannedText.startsWith("N")) {
                id = scannedText.substring(0, 16);
                first = scannedText.substring(15, 35).trim();
                last = scannedText.substring(35, 61).trim();
                rank = scannedText.substring(69, 75).trim();
            } else {
                id = scannedText.substring(0, 16);
                first = scannedText.substring(16, 36).trim();
                last = scannedText.substring(37, 63).trim();
                rank = scannedText.substring(74, 80).trim();
            }

            // This will be null if the employee is not loaded.
            Employee emp = Employee.getEmployeeFromUID(id);

            if (emp != null) {  // Represents employee exists and a list entry should be made
                if (bldgChk.isSelected()) {// and this is a  bldg in/out
                    if (emp.isInside()) {  // if employee is inside currently
                        emp.setInside(false);  // check out
                    } else {
                        emp.setInside(true);  // check in
                    }
                }

                if (bedCheck.isSelected()) {
                    if (emp.getBed() == null) {
                        bedCheck.setSelected(false);
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Pilsung Beddown");
                        alert.setHeaderText("Unable to Check Member into Beddown");
                        alert.setContentText("Airmen has no assigned bed.");
                        alert.show();
                    } else {
                        if (emp.isInBed()) {  // if employee is in beddown status
                            emp.setIsInBed(false);  // check out
                        } else {
                            emp.setIsInBed(true);  // check in
                        }
                    }
                }

                Session.getSession().addEntry(new ListEntry(emp, new DateTime(), bldgChk.isSelected(), emp.isInside(), bedCheck.isSelected(), emp.isInBed()));
                update();
                logger.log(Level.INFO, "Employee {0} checked in", emp.getName());
            } else { // represents a new employee that needs to be send to the NEW_EMPLOYEE screen
                ScannedData sd = new ScannedData(id, first, last, rank);
                logger.log(Level.INFO, "New employee scanned: {0}", sd);

                windowManager.passNewEmployeeData(sd);
                windowManager.selectWindow(WindowManager.BeddownWindow.NEW_AIRMEN);
                windowManager.setWindowLock(true);
            }
        } else {
            //not a valid scan
            logger.log(Level.INFO, "Invalid scan: {0}", scannedText);
        }
        scanBox.clear();
    }

    @Override
    public void update() {
        Platform.runLater(() -> WindowUtils.updateObservableListView(entryListView, Session.getSession().getEntries()));
        scanBox.requestFocus();
    }
}
