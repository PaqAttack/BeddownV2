package com.paqattack.gui_template.data;

import java.util.ArrayList;
import java.util.List;

public class Workcenter {
    private String name;
    private static List<Workcenter> workcenters = new ArrayList<>();

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
    public static List<String> getWorkcenterNames() {
        return workcenters.stream().map(Workcenter::getName).toList();
    }
}
