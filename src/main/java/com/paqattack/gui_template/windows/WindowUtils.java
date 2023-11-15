package com.paqattack.gui_template.windows;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

public class WindowUtils {
    private static final Logger logger = Logger.getLogger(WindowUtils.class.getName());

    public static final String FXML_PATH = "/com/paqattack/gui_template/windowFXMLs/";

    private WindowUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static <T> void showStatusLabel(Label label, String color, String msg) {
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
}
