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

    public Bed (String name, Gender gender) {
        this.gender = gender;
        this.name = name;
        this.uid = "0";
        logger.log(Level.INFO, "Bed {0} created", name);
    }

    public Bed (String name, Gender gender, String uid) {
        this.gender = gender;
        this.name = name;
        this.uid = uid;
        logger.log(Level.INFO, "Bed {0} created", name);
    }

    public Bed (String name, String input) {
        input = input.toUpperCase();
        if (input.startsWith("F")) {
            this.gender = Gender.FEMALE;
        } else {
            this.gender = Gender.MALE;
        }
        this.name = name;
        this.uid = "0";
        logger.log(Level.INFO, "Bed {0} created", name);
    }

    public Bed (String name, Employee employee) {
        if (employee.getGender() == Gender.FEMALE) {
            this.gender = Gender.FEMALE;
        } else {
            this.gender = Gender.MALE;
        }
        occupier = employee;
        assigned = true;
        this.name = name;
        this.uid = employee.getUID();
        logger.log(Level.INFO, String.format("Occupied bed %s created with employee: %s", name, getOccupierName()));
    }

    public Gender getGender() {
        return gender;
    }

    public String getGenderStr() {
        if (gender == Gender.FEMALE) {
            return "Female";
        } else {
            return "Male";
        }
    }

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

    private static void processLine(String line) {
        try {
            String[] bed = line.split(",");
            Session.getSession().addBed(new Bed(bed[0], bed[1]));
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error creating bed from file - {0}", e.getMessage());
        }
    }

    public boolean assign(Employee employee) {
        if (assigned) {
            logger.log(Level.WARNING, String.format("Bed %s is already assigned to %s", name, getOccupierName()));
            return false;
        } else {
            assigned = true;
            occupier = employee;
            logger.log(Level.INFO, String.format("Bed %s assigned to %s", name, getOccupierName()));
            return true;
        }
    }

    public String getOccupierName() {
        return occupier.getName();
    }

    public String getUid() {
        return uid;
    }
}
