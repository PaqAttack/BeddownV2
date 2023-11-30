package com.paqattack.gui_template.data;

import org.joda.time.DateTime;

public class ListEntry {
    private final Employee employee;
    private final DateTime time;
    private boolean checkInBldg = false;
    private boolean checkInBeddown = false;
    private boolean bedEvent = false;
    private boolean bldgEvent = false;

    /**
     * Creates a new list entry.
     * @param employee Employee reflected by event
     * @param time The time this event occured
     * @param buildingEvent was this a building in/out event?
     * @param checkInBldg Did they check in
     * @param beddownEvent Was this a beddown in/out event
     * @param checkInBeddown Did they check in (to beddown)
     */
    public ListEntry(Employee employee, DateTime time, boolean buildingEvent, boolean checkInBldg, boolean beddownEvent, boolean checkInBeddown) {
        bldgEvent = buildingEvent;
        bedEvent = beddownEvent;
        this.employee = employee;
        this.checkInBldg = checkInBldg;
        this.checkInBeddown = checkInBeddown;
        this.time = time;
    }

    public DateTime getTime() {
        return time;
    }

    /**
     * Converts elements of a DateTime object to the actual object. This is used when loading a dateTime from file
     * as the elements are stored and saved in an array of ints and then converted back using this method.
     * @param year int
     * @param month int
     * @param day int
     * @param hour int
     * @param minute int
     * @param second int
     * @return DateTime object
     */
    public static DateTime getTime(int year, int month, int day, int hour, int minute, int second) {
        return new DateTime(year, month, day, hour, minute, second);
    }

    /**
     * Return array with ints representing the time
     *
     * @param date date to be converted
     * @return array with ints representing the time in the
     *         order: year, month, day, hour, minute, second
     */
    public static int[] getTimeFromDateTime(DateTime date) {
        int[] time = new int[6];
        time[0] = date.getYear();
        time[1] = date.getMonthOfYear();
        time[2] = date.getDayOfMonth();
        time[3] = date.getHourOfDay();
        time[4] = date.getMinuteOfHour();
        time[5] = date.getSecondOfMinute();
        return time;
    }

    public Employee getEmployee() {
        return employee;
    }
    public boolean isCheckInBldg() {
        return checkInBldg;
    }
    public boolean isCheckInBeddown() {
        return checkInBeddown;
    }
    public boolean isBedEvent() {
        return bedEvent;
    }
    public boolean isBldgEvent() {
        return bldgEvent;
    }

    /**
     * Handles how events are displayed on the check in/out page.
     * @return String
     */
    @Override
    public String toString() {
        String status = employee.getName(20) + "    " + getTime().toString("HH:mm:ss dd-MMM-yy") + "    ";

        String bldg;
        if (bldgEvent) {
            if (checkInBldg) {
                bldg = "Checked In";
            } else {
                bldg = "Checked Out";
            }
        } else {
            bldg = "   N/A    ";
        }

        String beddown;
        if (bedEvent) {
            if (checkInBeddown) {
                beddown = "Checked In";
            } else {
                beddown = "Checked Out";
            }
        } else {
            beddown = "   N/A    ";
        }

        return status + "    " + bldg + "    " + beddown;
    }

}
