package com.paqattack.gui_template.data;

import org.joda.time.DateTime;

public class ListEntry {
    private Employee employee;
    private DateTime time;
    private boolean checkInBldg = false;
    private boolean checkOutBldg = false;
    private boolean checkInBeddown = false;
    private boolean checkOutBeddown = false;

    /**
     * Constructor for a check-in event
     *
     * @param employee
     * @param time
     */
    public ListEntry(Employee employee, DateTime time, boolean checkInEvent, boolean beddownEvent) {
        this.employee = employee;

        // The employee must be updated before the check-in event is recorded
        if (checkInEvent) {
            this.checkInBldg = employee.isInside();
            this.checkOutBldg = !employee.isInside();
        }
        if (beddownEvent) {
            this.checkInBeddown = employee.isInBed();
            this.checkOutBeddown = !employee.isInBed();
        }

        this.time = time;
    }

    public ListEntry(Employee employee, DateTime time, boolean checkInBldg, boolean checkOutBldg, boolean checkInBeddown, boolean checkOutBeddown) {
        this.employee = employee;
        this.checkInBldg = checkInBldg;
        this.checkOutBldg = checkOutBldg;
        this.checkInBeddown = checkInBeddown;
        this.checkOutBeddown = checkOutBeddown;
        this.time = time;
    }

    public DateTime getTime() {
        return time;
    }

    public static DateTime getTime(int year, int month, int day, int hour, int minute, int second) {
        return new DateTime(year, month, day, hour, minute, second);
    }

    /**
     * Return array with ints representing the time
     *
     * @param date
     * @return
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

    public boolean isCheckOutBldg() {
        return checkOutBldg;
    }

    public boolean isCheckInBeddown() {
        return checkInBeddown;
    }

    public boolean isCheckOutBeddown() {
        return checkOutBeddown;
    }

    @Override
    public String toString() {
        String status = employee.getName(20) + "    " + getTime().toString("HH:mm:ss dd-MMM-yy") + "    ";

        String bldg = null;
        if (checkInBldg) {
            bldg = "Checked In";
        } else {
            bldg = "   N/A    ";
        }

        String beddown = null;
        if (checkInBldg) {
            beddown = "Checked In";
        } else {
            beddown = "   N/A    ";
        }

        return status + "    " + bldg + "    " + beddown;
    }

}
