package com.paqattack.gui_template.data;

import com.paqattack.gui_template.Session;

import java.util.logging.Logger;

public class Employee {
    private static final Logger logger = Logger.getLogger(Employee.class.getName());

    private String name;
    private String uid;
    private Gender gender;
    private Rank rank;
    private Workcenter workcenter;
    private Bed bed;
    private boolean inside = true;
    private boolean inBed = false;

    public Employee(String uid, String name, Rank rank, Gender gender, Workcenter workcenter) {
        this.uid = uid;
        this.name = name;
        this.rank = rank;
        this.gender = gender;
        this.workcenter = workcenter;
        bed = null;
    }

    public Employee(String uid, String name, Rank rank, Gender gender, Workcenter workcenter, Bed bed) {
        this.uid = uid;
        this.name = name;
        this.rank = rank;
        this.gender = gender;
        this.workcenter = workcenter;
        if (bed.assign(this)) {
            this.bed = bed;
        } else {
            logger.warning("Bed " + bed.getName() + " is already assigned to " + bed.getOccupierName());
            this.bed = null;
        }

    }

    public static Employee getEmployeeFromUID(String s) {
        for (Employee employee : Session.getSession().getEmployees()) {
            if (employee.getUID().equalsIgnoreCase(s)) {
                return employee;
            }
        }
        return null;
    }

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

    public void setUID(String uid) {
        this.uid = uid;
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

    public void setBed(Bed bed) {
        this.bed = bed;
    }

    public boolean isInside() {
        return inside;
    }

    public void setInside(boolean inside) {
        this.inside = inside;
    }

    public boolean isInBed() {
        return inBed;
    }
}