package com.paqattack.gui_template.windows;

import com.paqattack.gui_template.Session;
import com.paqattack.gui_template.WindowManager;
import com.paqattack.gui_template.data.ListEntry;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.joda.time.DateTimeComparator;

import java.util.ArrayList;
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
    Button clearScanBtn;
    @FXML
    ListView<ListEntry> entryListView;

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

    private List<String> getStrings() {
        List<String> strings = new ArrayList<>();

        strings.add("Paquin, Christopher     44:44:44 dd-MMM-yy    Checked In    Checked In");
        strings.add("Paquin, Christopher     44:44:44 dd-MMM-yy       N/A        Checked In");
        strings.add("Paquin, Christopher     44:44:44 dd-MMM-yy    Checked In       N/A    ");
        strings.add("Paquin, Christopher     44:44:44 dd-MMM-yy    Checked In       N/A    ");
        strings.add("Paquin, Christopher     44:44:44 dd-MMM-yy    Checked In       N/A    ");
        strings.add("Paquin, Christopher     44:44:44 dd-MMM-yy    Checked In       N/A    ");
        strings.add("Paquin, Christopher     44:44:44 dd-MMM-yy    Checked In       N/A    ");
        strings.add("Paquin, Christopher     44:44:44 dd-MMM-yy    Checked In       N/A    ");
        strings.add("Paquin, Christopher     44:44:44 dd-MMM-yy    Checked In       N/A    ");

        return strings;
    }

    @Override
    public void update() {
        Platform.runLater(() -> WindowUtils.updateObservableListView(entryListView, Session.getSession().getEntries()));
        scanBox.requestFocus();
    }
}
