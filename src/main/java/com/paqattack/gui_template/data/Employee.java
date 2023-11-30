package com.paqattack.gui_template.data;

import com.paqattack.gui_template.Session;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Employee {
    private static final Logger logger = Logger.getLogger(Employee.class.getName());

    private String name;
    private String uid;
    private Gender gender;
    private Rank rank;
    private Workcenter workcenter;
    private Bed bed;
    private boolean noBed = false;
    private boolean inside = false;
    private boolean inBed = false;

    /**
     * Creates an employee object with no assigned bed.
     * @param uid Unique identifier of the Employee. This is the first 16 characters of the scanned ID
     * @param name LAST, FIRST name format
     * @param rank Rank of the Employee
     * @param gender Gender object of the Employee to ensure proper bed is assigned.
     * @param workcenter Work-center of Employee.
     */
    public Employee(String uid, String name, Rank rank, Gender gender, Workcenter workcenter) {
        this.uid = uid;
        this.name = name;
        this.rank = rank;
        this.gender = gender;
        this.workcenter = workcenter;
        bed = null;
        noBed = true;
    }

    /**
     * Creates an employee object with assigned bed.
     * @param uid Unique identifier of the Employee. This is the first 16 characters of the scanned ID
     * @param name LAST, FIRST name format
     * @param rank Rank of the Employee
     * @param gender Gender object of the Employee to ensure proper bed is assigned.
     * @param workcenter Work-center of Employee.
     * @param bed Bed object to assign member to.
     */
    public Employee(String uid, String name, Rank rank, Gender gender, Workcenter workcenter, Bed bed) {
        this.uid = uid;
        this.name = name;
        this.rank = rank;
        this.gender = gender;
        this.workcenter = workcenter;

        // Attempt to assign bed. This will be false if bed is already assigned.
        if (bed.assign(this)) {
            this.bed = bed;
            noBed = false;
        } else {
            logger.warning("Bed " + bed.getName() + " is already assigned to " + bed.getOccupierName());
            this.bed = null;
            noBed = true;
        }
    }

    /**
     * Searches employee list and returns the employee object associated with the provided UID.
     * @param uid This is the UID that is being searched for.
     * @return Employee object with matching UID.
     */
    public static Employee getEmployeeFromUID(String uid) {
        for (Employee employee : Session.getSession().getEmployees()) {
            if (employee.getUID().equalsIgnoreCase(uid)) {
                return employee;
            }
        }
        return null;
    }

    /**
     * Returns the name of the employee adjusted to a set length for easy formatting.
     * Names under the set length will have " " appended to meet the minimum and
     * names longer than the set length will be cut off at the proper length.
     * @param len integer number of characters to be displayed.
     * @return String with correct length.
     */
    public String getName(int len) {
        StringBuilder sb = new StringBuilder(name);
        if (sb.length() < len) {
            while (sb.length() < len) {
                sb.append(' ');
            }
        }
        sb.setLength(len);
        return sb.toString().toUpperCase();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUID() {
        return uid;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Rank getRank() {
        return rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }

    public Workcenter getWorkcenter() {
        return workcenter;
    }

    public void setWorkcenter(Workcenter workcenter) {
        this.workcenter = workcenter;
    }

    public Bed getBed() {
        return bed;
    }

    /**
     * Assigns bed to this employee
     * Bed will be validated as empty and connections will be fixed.
     * @param bed The bed to be assigned to this Employee
     */
    public void setBed(Bed bed) {
        if (bed.assign(this)) {
            this.bed = bed;
            noBed = false;
        } else {
            String error = "Bed (" + bed.getName() + ") already assigned to " + bed.getOccupierName() + ". Cant be assigned to " + this.getName() + " this way.";
            logger.log(Level.WARNING, error);
        }
    }

    public boolean isInside() {
        return inside;
    }

    public void setIsInBed(boolean status) {
        inBed = status;
    }

    public void setInside(boolean inside) {
        this.inside = inside;
    }

    public boolean isInBed() {
        return inBed;
    }

    /**
     * Unassigns the currently assigned bed from an Employee
     */
    public void unassignBed() {
        if (bed != null) {
            bed.unassign();
            bed = null;
            inBed = false;
            noBed = true;
        }
    }

    @Override
    public String toString() {
        return rank.getAbbreviation() + " " + name;
    }
}
