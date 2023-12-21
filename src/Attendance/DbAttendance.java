/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Attendance;

import database.DatabaseConnection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 *
 * @author Harsha
 */
public class DbAttendance {

    public AttendanceCounts loadAttenanceCount(String date) {
        AttendanceCounts attCount = new AttendanceCounts();

        try {

            int totalEmpCount = 0;

            //Loop All Employess
            ResultSet r = DatabaseConnection.Search("Select * From Employees ");
            while (r.next()) {
                totalEmpCount++;
                String empid = r.getString("EmpID");
                String PermanentStatus = r.getString("PermanentStatus");
                String ScannerID = r.getString("ScannerID");

                int count = 0;
                ResultSet r1 = DatabaseConnection.Search("Select Count(ID) As Count From Attendance WHERE Date = '" + date + "' And EmpId= '" + empid + "' And ScannerID= '" + ScannerID + "' ");
                while (r1.next()) {
                    count = r1.getInt("Count");
                }

                //Absent
                if (count == 0) {
                    attCount.setAbsentTotal(attCount.getAbsentTotal() + 1);

                    if (PermanentStatus.equals("Permanent")) {
                        attCount.setAbsentPermanent(attCount.getAbsentPermanent() + 1);

                    } else {
                        attCount.setAbsentNonPermanent(attCount.getAbsentNonPermanent() + 1);
                    }
                }

                //Present
                if (count > 0) {
                    attCount.setPresentTotal(attCount.getPresentTotal() + 1);

                    if (PermanentStatus.equals("Permanent")) {
                        attCount.setPresentPermanent(attCount.getPresentPermanent() + 1);

                    } else {
                        attCount.setPresentNonPermanent(attCount.getPresentNonPermanent() + 1);
                    }

                    //In and Out
                    if ((count & 1) == 0) {
                        //even: it means out
                        attCount.setTotalOut(attCount.getTotalOut() + 1);

                        if (PermanentStatus.equals("Permanent")) {
                            attCount.setPermanentOut(attCount.getPermanentOut() + 1);

                        } else {
                            attCount.setNonPermanentOut(attCount.getNonPermanentOut() + 1);
                        }

                    } else {
                        //odd: it means in
                        attCount.setTotalIn(attCount.getTotalIn() + 1);

                        if (PermanentStatus.equals("Permanent")) {
                            attCount.setPermanentIn(attCount.getPermanentIn() + 1);

                        } else {
                            attCount.setNonPermanentIn(attCount.getNonPermanentIn() + 1);
                        }
                    }
                }

            }

            attCount.setTotalEmps(totalEmpCount);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return attCount;
    }

    ArrayList<AttendanceTable> attTablItemList = new ArrayList<>();
    public ArrayList<AttendanceTable> GetAttTableData(String date, String SearchString, String PerStatus, String InOutStatus) {

        
        attTablItemList.clear();

        try {

            //Loop All Employess
            ResultSet r;
            if (PerStatus.equals("<html><b>All Employees</b></html>")) {
                r = DatabaseConnection.Search("SELECT * FROM Employees WHERE EmpId Like '%" + SearchString + "%' OR Name Like '%" + SearchString + "%' ");
            } else {
                r = DatabaseConnection.Search("SELECT * FROM Employees WHERE (EmpId Like '%" + SearchString + "%' OR Name Like '%" + SearchString + "%') And PermanentStatus = '" + PerStatus + "' ");
            }

            while (r.next()) {

                String PermanentStatus = r.getString("PermanentStatus");

                if (PermanentStatus.equals("Permanent")) {
                    PermanentStatus = "&nbsp <span style=\" color: #84BBC0; font-size:80%; \">&nbsp Permanent &nbsp </span></html>";
                } else {
                    PermanentStatus = "";
                }

                String empid = r.getString("EmpID");
                String empidOutPut = "<html>" + empid.replaceFirst("(?i)" + Pattern.quote(SearchString), "<span style=\"background-color: #E3F602 ;\">$0</span>") + "</html>";
                String name = r.getString("Name");
                String nameOutPut = "<html>" + name.replaceFirst("(?i)" + Pattern.quote(SearchString), "<span style=\"background-color: #E3F602 ;\">$0</span>")
                        + PermanentStatus;

                String ScannerID = r.getString("ScannerID");

                int count = 0;
                ResultSet r1 = DatabaseConnection.Search("Select Count(ID) As Count From Attendance Where Date = '" + date + "' And EmpId= '" + empid + "' And ScannerID= '" + ScannerID + "' ");
                while (r1.next()) {
                    count = r1.getInt("Count");
                }

                String Status = "";
                String StatusIf = "";

                //Absent
                if (count == 0) {
                    Status = "<html>&nbsp &nbsp <span style=\" background-color: #EAEEF1; color: #6C6C6C; \">&nbsp Absent &nbsp </span></html>";
                    StatusIf = "Absent";
                }

                System.out.println("empid " + empid);
                //Present
                if (count > 0) {

                    int remainder = (count & 1);
                    System.out.println("remainder : " + remainder);
                    if ((count & 1) == 0) {
                        //even: it means out
                        System.out.println("EVEN: " + count);

                        String DepartureTime = "";
                        if (count > 1) {

                            ResultSet r3 = DatabaseConnection.Search("Select Time From Attendance Where Date = '" + date + "' "
                                    + "And EmpId= '" + empid + "' And ScannerID= '" + ScannerID + "'  ORDER BY id DESC LIMIT 1");

                            while (r3.next()) {
                                DepartureTime = " Last Departure Time: " + r3.getString("Time");
                            }

                        }

                        Status = "<html>&nbsp &nbsp <span style=\" background-color: #D9778B; color: #FAF6F6; \">&nbsp Visit Out &nbsp </span> "
                                + " &nbsp  &nbsp <span style=\" color: #2F6FAF; font-size:80%; \">" + DepartureTime + "</span> </html>";

                        StatusIf = "Out";

                    } else {
                        //odd: it means in
                        System.out.println("ODD: " + count);

                        String DepartureTime = "";
                        if (count > 1) {

                            ResultSet r3 = DatabaseConnection.Search("Select Time From Attendance Where Date = '" + date + "' "
                                    + "And EmpId= '" + empid + "' And ScannerID= '" + ScannerID + "'  ORDER BY id DESC LIMIT 1 OFFSET 1");

                            while (r3.next()) {
                                DepartureTime = " Last Departure Time: " + r3.getString("Time");
                            }

                        }

                        String ArrivalTime = "";
                        ResultSet r4 = DatabaseConnection.Search("Select Time From Attendance Where Date = '" + date + "' "
                                + "And EmpId= '" + empid + "' And ScannerID= '" + ScannerID + "'  ORDER BY id DESC LIMIT 1");

                        while (r4.next()) {
                            ArrivalTime = " Last Arrival Time: " + r4.getString("Time");
                        }

                        Status = "<html>&nbsp &nbsp <span style=\" background-color: #54BE93; color: #FAF6F6; \">&nbsp Present &nbsp </span> "
                                + " &nbsp  &nbsp <span style=\" color: #2F6FAF; font-size:80%; \">" + DepartureTime + "</span>"
                                + " &nbsp  &nbsp <span style=\" color: #356853; font-size:80%; \">" + ArrivalTime + "</span> </html>";
                        StatusIf = "In";
                    }
                }

                if (InOutStatus.equals("<html><b>All Status</b></html>")) {

                    AttendanceTable tableItem = new AttendanceTable();
                    tableItem.setEmpID(empidOutPut);
                    tableItem.setName(nameOutPut);
                    tableItem.setPermanentStatus(PermanentStatus);
                    tableItem.setStatus(Status);
                    attTablItemList.add(tableItem);

                } else if (InOutStatus.equals("Out Only")) {
                    if (StatusIf.equals("Out")) {
                        AttendanceTable tableItem = new AttendanceTable();
                        tableItem.setEmpID(empidOutPut);
                        tableItem.setName(nameOutPut);
                        tableItem.setPermanentStatus(PermanentStatus);
                        tableItem.setStatus(Status);
                        attTablItemList.add(tableItem);
                    }
                } else if (InOutStatus.equals("In Only")) {
                    if (StatusIf.equals("In")) {
                        AttendanceTable tableItem = new AttendanceTable();
                        tableItem.setEmpID(empidOutPut);
                        tableItem.setName(nameOutPut);
                        tableItem.setPermanentStatus(PermanentStatus);
                        tableItem.setStatus(Status);
                        attTablItemList.add(tableItem);
                    }
                } else if (InOutStatus.equals("Absent Only")) {
                    if (StatusIf.equals("Absent")) {
                        AttendanceTable tableItem = new AttendanceTable();
                        tableItem.setEmpID(empidOutPut);
                        tableItem.setName(nameOutPut);
                        tableItem.setPermanentStatus(PermanentStatus);
                        tableItem.setStatus(Status);
                        attTablItemList.add(tableItem);
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return attTablItemList;
    }
}
