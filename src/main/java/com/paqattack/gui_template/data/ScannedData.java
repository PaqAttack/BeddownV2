package com.paqattack.gui_template.data;

public class ScannedData {
    String id;
    String last;
    String first;
    String rank;
    boolean checkinBldg;
    boolean checkinBed;

    public ScannedData(String id, String last, String first, String rank, boolean checkInEvent, boolean beddownEvent) {
        this.id = id;
        this.last = last;
        this.first = first;
        this.rank = rank;
        this.checkinBldg = checkInEvent;
        this.checkinBed = beddownEvent;
    }

    public String getId() {
        return id;
    }

    public String getLast() {
        return last;
    }

    public String getFirst() {
        return first;
    }

    public String getRank() {
        return rank;
    }

    public boolean isCheckinBldg() {
        return checkinBldg;
    }

    public boolean isCheckinBed() {
        return checkinBed;
    }

    @Override
    public String toString() {
        return "ScannedData{" + "id=" + id + ", last=" + last + ", first=" + first + ", rank=" + rank + '}';
    }
}
