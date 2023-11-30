package com.paqattack.gui_template.data;

import com.paqattack.gui_template.Session;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Bed {
    private static final Logger logger = Logger.getLogger(Bed.class.getName());
    private final Gender gender;
    private String name;
    private boolean assigned = false;
    private Employee occupier = null;
    private String uid = "0";

    /**
     * Creates a new Bed with a bed name and a gender
     * @param name Name of bed object
     * @param gender gender of bed object
     */
    public Bed (String name, Gender gender) {
        this.gender = gender;
        this.name = name;
        this.uid = "0";
        logger.log(Level.INFO, "Bed {0} created", name);
    }

    /**
     * Creates a new Bed with a bed name, gender and UID of employee
     * This is done during loading of save files and the UID is used to connect employees after loading.
     * @param name Name of bed object
     * @param gender gender of bed object
     * @param uid Unique ID of Employee to eb assigned to bed
     */
    public Bed (String name, Gender gender, String uid) {
        this.gender = gender;
        this.name = name;
        this.uid = uid;
        logger.log(Level.INFO, "Bed {0} created", name);
    }

    /**
     * Gets gender assignment of bed
     * @return Gender object (Gender.Male / Gender.Female)
     */
    public Gender getGender() {
        return gender;
    }

    /**
     * Gets Name of the bed object
     * @return String of bed name
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAssigned() {
        return assigned;
    }

    public Employee getOccupier() {
        return occupier;
    }

    /**
     * Loads provided bed file (*.CSV). This file must be in the format of BED NAME,GENDER on each line.
     * Example: BED_1,Male
     * @param file Provided *.CSV file from which beds will be loaded.
     * @return boolean indicating success or failure.
     */
    public static boolean loadBedFile(File file) {

        if (file == null) {
            logger.log(Level.WARNING, "No file selected");
            return false;
        }

        if (file.exists()) {
            logger.log(Level.INFO, "Loading bed file: {0}", file.getAbsolutePath());
        } else {
            logger.log(Level.WARNING, "File does not exist: {0}", file.getAbsolutePath());
            return false;
        }

        //TODO Validate bed file

        // Load file and create beds
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                processLine(line);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error reading bed file: {0}", e.getMessage());
        }

        logger.log(Level.INFO, "Bed file loaded successfully");
        return true;
    }

    /**
     * Process the line from loadBedFile() and create bed. The line must be a 2 part line with a comma delimiter.
     * @param line String from the file being loaded.
     */
    private static void processLine(String line) {
        try {
            String[] bed = line.split(",");
            Session.getSession().addBed(new Bed(bed[0], Gender.getGenderFromStr(bed[1])));
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error creating bed from file - {0}", e.getMessage());
        }
    }

    /**
     * Assign bed to an employee
     * @param employee Employee to be assigned as the new occupier of the bed.
     * @return boolean indicating success or failure of assignment.
     */
    public boolean assign(Employee employee) {
        if (assigned) {  // If bed is already assigned...
            logger.log(Level.WARNING, String.format("Bed %s is already assigned to %s", name, getOccupierName()));
            return false;
        } else {  // If bed is free to be assigned...
            assigned = true;
            occupier = employee;
            logger.log(Level.INFO, String.format("Bed %s assigned to %s", name, getOccupierName()));
            return true;
        }
    }

    /**
     * Disconnects bed from any Employees
     */
    public void unassign() {
        assigned = false;
        occupier = null;
        uid = "0";
    }

    public String getOccupierName() {
        return occupier.getName();
    }

    public String getUid() {
        return uid;
    }

    @Override
    public String toString() {
        return name;
    }
}
