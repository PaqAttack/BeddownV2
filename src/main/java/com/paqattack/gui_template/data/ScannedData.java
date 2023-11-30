package com.paqattack.gui_template.data;

public class ScannedData {
    String id;
    String last;
    String first;
    String rank;

    /**
     * Creates a ScannedData object representing data scanned by the reader to be passed to the New_Employee scene
     * @param id UID of the member (first 16 characters of PDF417 barcode (front of CAC)
     * @param last String of last name of member
     * @param first string of first name of member
     * @param rank string of rank of member
     */
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

    /**
     * Used for logger only
     * @return String showing what data was scanned.
     */
    @Override
    public String toString() {
        return "ScannedData{" + "id=" + id + ", last=" + last + ", first=" + first + ", rank=" + rank + '}';
    }
}
