package com.paqattack.gui_template.windows;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

public class WindowUtils {
    private static final Logger logger = Logger.getLogger(WindowUtils.class.getName());

    /**
     * The path to the FXML files.
     */
    public static final String FXML_PATH = "/windowFXMLs/";

    private WindowUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Shows a status label for 2.5 seconds.
     * @param label The label to show.
     * @param color The color of the label. "G" for green, "R" for red.
     * @param msg The message to display.
     */
    public static void showStatusLabel(Label label, String color, String msg) {
        Timer timer = new Timer();
        if (color.startsWith("G")) {    // green
            label.setTextFill(Color.web("#0bb527"));
        } else {    //red
            label.setTextFill(Color.web("#d70606"));
        }
        label.setText(msg);
        label.setVisible(true);

        TimerTask hideLabel = new TimerTask() {
            @Override
            public void run() {
                label.setVisible(false);
            }
        };
        timer.schedule(hideLabel, 2500);
    }

    /**
     * Opens a file picker dialog and returns the path of the selected file.
     * @param window The window to attach the file picker to.
     * @param filter The file extension filter to use.
     * @param cmd The command to display in the file picker dialog.
     * @return The path of the selected file.
     */
    public static String saveFilePicker(Window window, FileChooser.ExtensionFilter filter, String cmd) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(cmd);

        // Getting the directory where the JAR resides
        String jarDir = new File(WindowUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();

        // Setting initial directory to the JAR's directory
        fileChooser.setInitialDirectory(new File(jarDir));

        // Setting the file extension filter
        //FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        if (filter != null) {
            fileChooser.getExtensionFilters().add(filter);
        }

        File selectedFile = fileChooser.showOpenDialog(window);

        if (selectedFile != null) {
            logger.log(java.util.logging.Level.INFO, "File path selected: " + selectedFile);
            return selectedFile.toString();
        } else {
            logger.log(java.util.logging.Level.WARNING, "No file selected");
            return null;
        }
    }

    public static <T> void updateObservableListView(ListView<T> listView, List<T> list) {
        if (listView != null && list != null) {
            ObservableList<T> observableList = FXCollections.observableArrayList(list);
            listView.setItems(observableList);
        }
    }
}
