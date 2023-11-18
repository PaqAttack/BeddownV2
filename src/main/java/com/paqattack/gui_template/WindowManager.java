package com.paqattack.gui_template;

import com.paqattack.gui_template.windows.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.logging.Level;
import java.util.logging.Logger;

public class WindowManager {
    private static final Logger logger = Logger.getLogger(WindowManager.class.getName());
    private Stage mainStage;
    private ResourceManager resourceManager;
    private Session session;
    private boolean windowLock = true;

    public enum BeddownWindow {
        STARTUP,
        BEDDOWN,
        CHECKINOUT,
        MANAGE,
        REPORTS
    }

    //region Windows
    MainWindow mainWindow = new MainWindow(this);
    Sidebar sidebar = new Sidebar(this);
    Beddown beddown = new Beddown(this);
    CheckInOut checkInOut = new CheckInOut(this);
    Manage manage = new Manage(this);
    Reports reports = new Reports(this);
    StartUp startUp = new StartUp(this);
    //endregion


    public WindowManager(Stage stage) {

        // Save stage
        mainStage = stage;

        // create session
        session = new Session(this);

        // Create resource manager
        logger.log(Level.INFO, "Creating resource manager");
        resourceManager = new ResourceManager(String.format("%s%s", System.getProperty("user.dir"), "/Resources/"), "/");

        // Initial Setup for Windows
        setupWindows();

        //set title
        mainStage.setTitle("Beddown Manager");
    }

    private void setupWindows() {
        // add windows together
        mainWindow.setLeft(sidebar);
        selectWindow(startUp);

        // Add scenes
        mainStage.setScene(new Scene(mainWindow));
    }

    public void start() {
        mainStage.setResizable(false);
        mainStage.show();
    }

    /**
     * selects a window to display in the main window.
     * Bypasses any locks in place
     * @param node
     */
    private void selectWindow(Node node) {
        mainWindow.setCenter(node);
    }

    /**
     * Selects a window to display in the main window.
     * Will not function if lock is in place
     * @param window The window to display.
     */
    public void selectWindow(BeddownWindow window) {
        if (windowLock) {
            logger.log(Level.INFO, "Window lock is in place. Cannot change window.");
            return;
        }
        switch (window) {
            case STARTUP -> mainWindow.setCenter(startUp);
            case BEDDOWN -> mainWindow.setCenter(beddown);
            case CHECKINOUT -> mainWindow.setCenter(checkInOut);
            case MANAGE -> mainWindow.setCenter(manage);
            case REPORTS -> mainWindow.setCenter(reports);
        }
    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    public Window getMainStage() {
        return mainStage;
    }

    public void setWindowLock(boolean lock) {
        windowLock = lock;
    }

    public Session getSession() {
        return session;
    }
}
