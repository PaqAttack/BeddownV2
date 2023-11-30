package com.paqattack.gui_template.data;

import java.util.ArrayList;
import java.util.List;

public class Workcenter {
    private String name;

    // possibly move this to Session...
    private static final List<Workcenter> workcenters = new ArrayList<>();

    /**
     * Creates a new workcenter object. If this is a new workcenter then it's added to the list of workcenters.
     * @param name
     */
    public Workcenter(String name) {
        this.name = name;
        boolean exists = false;
        for (Workcenter workcenter : workcenters) {
            if (workcenter.getName().equalsIgnoreCase(name)) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            addWorkcenter(this);
        }
    }

    public String getName() {
        return name;
    }

    private static void addWorkcenter(Workcenter workcenter) {
        workcenters.add(workcenter);
    }

    public static List<Workcenter> getWorkcenters() {
        return workcenters;
    }

    /**
     * Gets a list of Strings of each workcenter.
     * @return List<String>
     */
    public static List<String> getWorkcenterNames() {
        return workcenters.stream().map(Workcenter::getName).toList();
    }

    @Override
    public String toString() {
        return name;
    }
}
