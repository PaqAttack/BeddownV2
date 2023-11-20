package com.paqattack.gui_template.data;

public class ScannedData {
    String id;
    String last;
    String first;
    String rank;

    public ScannedData(String id, String last, String first, String rank) {
        this.id = id;
        this.last = last;
        this.first = first;
        this.rank = rank;
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

    @Override
    public String toString() {
        return "ScannedData{" + "id=" + id + ", last=" + last + ", first=" + first + ", rank=" + rank + '}';
    }
}
