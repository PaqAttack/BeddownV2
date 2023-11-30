package com.paqattack.gui_template.windows;

import com.paqattack.gui_template.Session;
import com.paqattack.gui_template.WindowManager;
import com.paqattack.gui_template.data.Employee;
import com.paqattack.gui_template.data.ListEntry;
import com.paqattack.gui_template.data.ScannedData;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import org.joda.time.DateTime;

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
    Button clearScanBtn;
    @FXML
    ListView<ListEntry> entryListView;
    @FXML
    CheckBox bedCheck;
    @FXML
    CheckBox bldgChk;
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

        backPane.setOnMouseMoved(event -> scanBox.requestFocus());
        clearScanBtn.setOnAction(event -> scanBox.clear());
        pause.setOnFinished(event -> processScan());
        scanBox.setOnKeyTyped(event -> extendTimer());
        bedCheck.setOnAction(event -> {
            if (!bedCheck.isSelected()) {
                bldgChk.setSelected(true);
            }
        });
    }

    private void extendTimer() {
        pause.stop();
        pause.playFromStart();
    }

    private void processScan() {
        String scannedText = scanBox.getText();
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

            Employee emp = Employee.getEmployeeFromUID(id);
            if (emp != null) {

                if (bldgChk.isSelected()) {// and this is a  bldg in/out
                    if (emp.isInside()) {  // if employee is inside currently
                        emp.setInside(false);  // check out
                    } else {
                        emp.setInside(true);  // check in
                    }
                }

                if (bedCheck.isSelected()) {
                    if (emp.isInBed()) {  // if employee is in beddown status
                        emp.setIsInBed(false);  // check out
                    } else {
                        emp.setIsInBed(true);  // check in
                    }
                }

                Session.getSession().addEntry(new ListEntry(emp, new DateTime(), bldgChk.isSelected(), emp.isInside(), bedCheck.isSelected(), emp.isInBed()));
                update();
                logger.log(Level.INFO, "Employee {0} checked in", emp.getName());
            } else {
                //new employee
                ScannedData sd = new ScannedData(id, first, last, rank, bldgChk.isSelected(), bedCheck.isSelected());
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
