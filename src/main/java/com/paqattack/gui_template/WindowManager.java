package com.paqattack.gui_template;

import com.paqattack.gui_template.data.ScannedData;
import com.paqattack.gui_template.windows.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.logging.Level;
import java.util.logging.Logger;

public class WindowManager {
    private static final Logger logger = Logger.getLogger(WindowManager.class.getName());
    private final Stage mainStage;
    private final ResourceManager resourceManager;
    private final Session session;
    private boolean windowLock = true;

    public enum BeddownWindow {
        STARTUP,
        STATUS,
        CHECKINOUT,
        MANAGE,
        NEW_PLAYER
    }

    //region Windows
    MainWindow mainWindow = new MainWindow(this);
    Sidebar sidebar = new Sidebar(this);
    Status status = new Status(this);
    CheckInOut checkInOut = new CheckInOut(this);
    Manage manage = new Manage(this);
    StartUp startUp = new StartUp(this);
    NewEmployee newEmployee = new NewEmployee(this);
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
     */
    private void selectWindow(Updatable node) {
        mainWindow.setCenter((Node) node);
        node.update();
    }

    /**
     * Selects a window to display in the main window.
     * Will not function if lock is in place
     *
     * @param window The window to display.
     */
    public void selectWindow(BeddownWindow window) {
        if (windowLock) {
            logger.log(Level.INFO, "Window lock is in place. Cannot change window.");
            return;
        }
        switch (window) {
            case STARTUP -> {
                mainWindow.setCenter(startUp);
                startUp.update();
            }
            case STATUS -> {
                mainWindow.setCenter(status);
                status.update();
            }
            case CHECKINOUT -> {
                mainWindow.setCenter(checkInOut);
                checkInOut.update();
            }
            case MANAGE -> {
                mainWindow.setCenter(manage);
                manage.update();
            }
            case NEW_PLAYER -> {
                mainWindow.setCenter(newEmployee);
                newEmployee.update();
            }
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

    public void passNewEmployeeData(ScannedData sd) {
        newEmployee.setScannedData(sd);
    }
}
