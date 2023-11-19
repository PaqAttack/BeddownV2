package com.paqattack.gui_template;

import com.paqattack.gui_template.data.*;
import javafx.stage.FileChooser;
import org.joda.time.DateTime;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Session {
    private static final Logger logger = Logger.getLogger(Session.class.getName());
    private final List<Bed> beds = new ArrayList<>();
    private final List<Employee> employees = new ArrayList<>();
    private final List<ListEntry> entries = new ArrayList<>();
    private static Session session;
    private final WindowManager windowManager;

    public Session(WindowManager windowManager) {
        this.windowManager = windowManager;
        logger.log(Level.INFO, "Session started");
        Session.setSession(this);
    }

    public void addBed(Bed bed) {
        beds.add(bed);
    }

    public void addEmployee(Employee employee) {
        employees.add(employee);
    }

    public void addEntry(ListEntry entry) {
        entries.add(entry);
    }

    public List<Bed> getBeds() {
        return beds;
    }

    public List<Bed> getAssignedBeds() {
        return beds.stream()
                .filter(Bed::isAssigned).toList();
    }

    public List<Bed> getUnassignedBeds() {
        return beds.stream()
                .filter(x -> !x.isAssigned()).toList();
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public List<String> getBldgCheckedInEmployeeNames() {
        return employees.stream()
                .filter(Employee::isInside)
                .map(Employee::toString)
                .toList();
    }

    public List<Employee> getBldgCheckedInEmployees() {
        return employees.stream()
                .filter(Employee::isInside)
                .toList();
    }

    public List<Employee> getBeddownCheckedInEmployees() {
        return employees.stream()
                .filter(Employee::isInBed).toList();
    }

    public List<ListEntry> getEntries() {
        entries.sort(Comparator.comparing(ListEntry::getTime));

        return entries;
    }

    public static Session getSession() {
        return session;
    }

    private static void setSession(Session session) {
        Session.session = session;
    }

    public boolean loadSaveFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("DAT Files", "*.DAT"));
        File file = fileChooser.showOpenDialog(windowManager.getMainStage());

        if (loadFile(file)) {
            windowManager.setWindowLock(false);
            windowManager.selectWindow(WindowManager.BeddownWindow.CHECKINOUT);
            return true;
        } else {
            return false;
        }
    }

    private boolean loadFile(File file) {
        if (file == null) {
            logger.log(Level.WARNING, "No file selected");
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file.toString()))) {
            String line;

            // Stages:
            // 1 = Beds
            // 2 = Personnel
            // 3 = list events

            int stage = 0;

            while ((line = reader.readLine()) != null) {

                if (line.startsWith("[")) {
                    stage = getStage(line);
                    continue;
                }

                switch (stage) {
                    case 1 -> {
                        // create bed
                        // name ; gender ; assigned 0 or designation
                        String[] bed = line.split(";");

                        processBed(bed);
                    }
                    case 2 -> {
                        String[] person = line.split(";");
                        // id ; name ; rank ; gender ; wce ; bed assigned 0 or designation

                        processEmployee(person);
                    }
                    case 3 -> {
                        String[] listEvent = line.split(";");

                        // uid ; time array 0-1-2-3-4-5 ; checkinBldg ; checkOutBldg ; checkIn Bed down ; checkIn Bed down

                        processEvent(listEvent);
                    }
                    default -> logger.log(Level.WARNING, "Unable to load line: {0}", line);
                }
            }

            // go through all beds and connect employees to their beds
            for (Bed bed : beds) {
                String uid = bed.getUid();

                if (uid != null && !uid.equalsIgnoreCase("0")) {
                    Employee emp = Employee.getEmployeeFromUID(uid);
                    if (emp != null) {
                        bed.assign(emp);
                        emp.setBed(bed);
                        logger.log(Level.INFO, "Assigned bed {0} to employee {1}", new Object[]{bed.getName(), uid});
                    } else {
                        logger.log(Level.WARNING, "Unable to assign bed {0} to employee {1}", new Object[]{bed.getName(), uid});
                    }

                }
            }
            return true;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Unable to load file: {0}", file);
            return false;
        }
    }

    private void processBed(String[] bed) {
        try {
            String name = bed[0];
            Gender gender = bed[1].toUpperCase().startsWith("F")? Gender.FEMALE : Gender.MALE;
            addBed(new Bed(name, gender, bed[2]));
            logger.log(Level.INFO, "Loaded bed: {0}", name);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Unable to load bed: {0}", bed[0]);
        }
    }

    private void processEvent(String[] listEvent) {
        try {
            Employee employee = Employee.getEmployeeFromUID(listEvent[0]);
            String[] date = listEvent[1].split("-");
            DateTime time = ListEntry.getTime(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]), Integer.parseInt(date[3]), Integer.parseInt(date[4]), Integer.parseInt(date[5]));

            boolean checkInBldg = listEvent[2].equalsIgnoreCase("1");
            boolean checkOutBldg = listEvent[3].equalsIgnoreCase("1");
            boolean checkInBeddown = listEvent[4].equalsIgnoreCase("1");
            boolean checkOutBeddown = listEvent[5].equalsIgnoreCase("1");

            ListEntry entry = new ListEntry(employee, time, checkInBldg, checkOutBldg, checkInBeddown, checkOutBeddown);
            addEntry(entry);
            logger.log(Level.INFO, "Loaded list entry: {0}", entry);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Unable to load list entry: {0}", listEvent[0]);
        }
    }

    private void processEmployee(String[] person) {
        try {
            Rank rank = new Rank(person[2]);
            Workcenter wce = new Workcenter(person[3]);
            Gender gender = person[3].toUpperCase().startsWith("F")? Gender.FEMALE : Gender.MALE;

            Employee emp = new Employee(person[0], person[1], rank, gender, wce);
            addEmployee(emp);
            logger.log(Level.INFO, "Loaded employee: {0}", emp.getName());
        } catch (Exception e) {
            logger.log(Level.WARNING, "Unable to load employee: {0}", person[0]);
        }
    }

    private int getStage(String header) {
        // Stages:
        // 1 = Beds
        // 2 = Personnel
        // 3 = list events

        if (header.equalsIgnoreCase("[PSNL]")) {
            return 2;
        } else if (header.equalsIgnoreCase("[BEDS]")) {
            return 1;
        } else if (header.equalsIgnoreCase("[LSTE]")) {
            return 3;
        } else {
            return -1;
        }
    }

    public boolean saveSessionFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("DAT Files", "*.DAT"));
        File file = fileChooser.showSaveDialog(windowManager.getMainStage());

        if (saveFile(file)) {
            windowManager.setWindowLock(false);
            windowManager.selectWindow(WindowManager.BeddownWindow.CHECKINOUT);
            return true;
        } else {
            return false;
        }
    }

    private boolean saveFile(File file) {
        String newline = System.getProperty("line.separator");

        if (file == null) {
            logger.log(Level.WARNING, "No file selected");
            return false;
        }

        try (BufferedWriter bw = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {

            // write header & beds
            bw.append("[BEDS]").append(newline);

            // name ; gender ; assigned 0 or designation
            for (Bed bed : beds) {
                String currentBed = bed.getName() + ";" + bed.getGenderStr() + ";" + bed.getUid() + newline;
                bw.append(currentBed);
            }

            // write header & psnl
            bw.append("[PSNL]").append(newline);

            for (Employee value : employees) {      // id ; name ; rank ; gender ; wce ; bed assigned 0 or designation
            String gender = value.getGender() == Gender.FEMALE ? "F" : "M";
                String employee = value.getUID() + ";" + value.getName() + ";" + value.getRank().getAbbreviation() + ";" + gender + ";" + value.getWorkcenter().getName() + newline;
                bw.append(employee);
            }

            // write header & entrylist
            bw.append("[LSTE]").append(newline);

            for (ListEntry le : entries) {      // uid ; timearray 0-1-2-3-4-5 ; checkinBldg ; checkOutBldg ; checkInBeddown ; checkInBeddown
                int[] time = ListEntry.getTimeFromDateTime(le.getTime());
                String timeStr = time[0] + "-" + time[1] + "-" + time[2] + "-" + time[3] + "-" + time[4] + "-" + time[5];

                String currentList = le.getEmployee().getUID() + ";" + timeStr + ";" + (le.isCheckInBldg()? "1" : "0") + ";" + (le.isCheckOutBldg()? "1" : "0") + ";" + (le.isCheckInBeddown()? "1" : "0") + ";" + (le.isCheckOutBeddown()? "1" : "0") + newline;
                bw.append(currentList);
            }

            logger.log(Level.INFO, "Save file created successfully");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Unable to create save file: {0}", file);
            return false;
        }
        return true;
    }
}
