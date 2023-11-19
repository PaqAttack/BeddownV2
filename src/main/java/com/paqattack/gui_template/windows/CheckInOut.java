package com.paqattack.gui_template.windows;

import com.paqattack.gui_template.Session;
import com.paqattack.gui_template.WindowManager;
import com.paqattack.gui_template.data.Employee;
import com.paqattack.gui_template.data.ListEntry;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.joda.time.DateTime;

import java.util.Timer;
import java.util.TimerTask;
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
    private Timer timer = new Timer();
    private boolean timerActive = false;

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

    }

    private void extendTimer() {

    }

    public void onChange() {

    }

    public void scannedChange() {
        if (scanBox.getText().length() == 99) {
            String scannedID = scanBox.getText();

            if (scannedEmployee != null) {
                session.createListEntry(scannedEmployee, ListEntry.convertDateToString(new DateTime()));
                updateAccountabilityInterfaceElements();
                System.out.println("Created list entry");
            } else {
                // fill blocks with si data
                transferSIData(si);
            }
            scantxt.setText("");
        }
    }

    @Override
    public void update() {
        Platform.runLater(() -> WindowUtils.updateObservableListView(entryListView, Session.getSession().getEntries()));
        scanBox.requestFocus();
    }
}
