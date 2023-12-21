/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Scanners;

import UI.MainFrame;
import UI.Splash;
import database.DatabaseConnection;
import helper.SaveErrors;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Harsha
 */
public class ReadScanner {

    public void ReadAllScanners() {
        try {
            ArrayList<Scanner> scanners = new ArrayList<>();

            ResultSet r5 = DatabaseConnection.Search("Select * From Scanners ");
            while (r5.next()) {

                Scanner scnr = new Scanner();

                scnr.setScannerID(r5.getString("ID"));
                scnr.setName(r5.getString("Scanner_Name"));
                scnr.setIP(r5.getString("IP_Address"));
                scnr.setPort(r5.getString("Port"));

                scanners.add(scnr);
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (Scanner scanner : scanners) {

                        try {
                            MainFrame.txtStatus.setText("Reading Scanner " + scanner.getName() + " "
                                    + "[id " + scanner.getScannerID() + "]");
                            ConnectAndReadScanner(scanner);

                            //Splash.f1.RefreshAllData();
                            Thread.sleep(5000);//5sec
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ReadScanner.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
            SaveErrors.SaveErrorLog(e);
        }
    }

    private void ConnectAndReadScanner(Scanner scanner) {
        try {

            int logcount = 0;
            String consoleRead = "reading"; //remove content

            System.out.println("PROCESS STARTED");
            ProcessBuilder pb = new ProcessBuilder();
            pb.command("AttLogs.exe", scanner.getIP(), scanner.getPort(), "Silent");

            //PROCESS START
            final Process p = pb.start();
            int exitCode = p.waitFor(); // wait for the process to complete

            System.out.println("PROCESS FINISHED");
            //Read Text File And Save To DB and checking return error msg
            if (consoleRead.equals("NoData")) {
                /////frmMain.showMSG("'" + jcmbScanner.getSelectedItem() + "' Scanner Error : 'No Data From Terminal Returns!!'", 1);
                DatabaseConnection.Update("Insert into ReadLog (Scanner, Date,Time, Description, User) "
                        + "Values ("
                        + "'" + scanner.getScannerID() + "', "
                        + "'" + MainFrame.SysDate + "', "
                        + "'" + MainFrame.SysTime + "', "
                        + "'" + "Error: No data returns form the terminal!" + "', "
                        + "'" + MainFrame.UserNow + "')");
                /////LoadScannerLog();
                //jbtnRead.setEnabled(true);
            } else if (consoleRead.equals("ReadingDataFaild")) {
                //frmMain.showMSG("'"+ jcmbScanner.getSelectedItem() + "' Scanner Error : 'Reading Data From Terminal Failed!!'", 1);
                DatabaseConnection.Update("Insert into ReadLog (Scanner, Date,Time, Description, User) "
                        + "Values ("
                        + "'" + scanner.getScannerID() + "', "
                        + "'" + MainFrame.SysDate + "', "
                        + "'" + MainFrame.SysTime + "', "
                        + "'" + "Error: Connection Faild!" + "', "
                        + "'" + MainFrame.UserNow + "')");

                /////LoadScannerLog();
            } else if (consoleRead.equals("reading")) {  //READ ATTENDACNE TEXT FILE AND SAVE INTO DB  

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d", Locale.ENGLISH);
                SimpleDateFormat parser = new SimpleDateFormat("HH:mm");

                String ProgramDataPath = System.getenv("ProgramData") + "\\Interithm\\Attendance Monitor\\local\\";
                File file = new File(ProgramDataPath + "AT_LOG.txt"); //ATTENDANCE TEXT FILE

                //checking file is exist
                if (file.exists()) {
                    System.out.println("FILE FOUND");
//                    java.util.Scanner sc = new java.util.Scanner(file);
//                    while (sc.hasNextLine()) { //SCANE FILE
//                        //ignore empty lines
//
//                        if (sc.nextLine() != null && !sc.nextLine().trim().isEmpty()) {
//                            String[] splitSt = sc.nextLine().trim().split(";");
//                            if (splitSt.length >= 3) {
//                                //String [] splitSt =sc.nextLine().trim().split(",");                           
//                                String[] splitSt2 = splitSt[3].split(" ");
//
//                                //System.out.println("LineNo: " + splitSt[0]+"   UserID: " + splitSt[1]+"   InOut Code: " + splitSt[2]+"   Date: " + splitSt2[0] +"   Time: " + splitSt2[1]);                              
//                                //CONVERT DATE 
//                                LocalDate date = LocalDate.parse(splitSt2[0], formatter);
//                                //CONVERT TIME
//                                Date TIME = parser.parse(splitSt2[1]);
//
//                                //ADD TO TEMP ATTENDACE LOG TABLE
////                    DatabaseConnection.Update("Insert into Attendance (Emp_ID, Count, In_Out_Mode, Date, Time) Values ('" + splitSt[1] + "', '" + splitSt[0].trim() + "','" + splitSt[2] + "','" + date + "', '" + parser.format(TIME) + "')");
//                                DatabaseConnection.Update("INSERT INTO Attendance (ScannerID, EmpID, Date, Time) VALUE ('" + scanner.getScannerID() + "', '" + splitSt[1].trim() + "', '" + date + "', '" + parser.format(TIME) + "')");
//
//                                logcount++; //COUNT LOOP
//
//                            }
//
//                        }
//
//                    }

                    List<String> lines = Collections.emptyList();
                    lines = Files.readAllLines(file.toPath());

                    for (String st : lines) {

                        if (!st.trim().isEmpty()) {
                            System.out.println(st);
                            System.out.println(">>>");

                            String[] splitSt = st.trim().split(";");
                            if (splitSt.length >= 3) {
                                //String [] splitSt =sc.nextLine().trim().split(",");                           
                                String[] splitSt2 = splitSt[3].split(" ");

                                //System.out.println("LineNo: " + splitSt[0]+"   UserID: " + splitSt[1]+"   InOut Code: " + splitSt[2]+"   Date: " + splitSt2[0] +"   Time: " + splitSt2[1]);                              
                                //CONVERT DATE 
                                LocalDate date = LocalDate.parse(splitSt2[0], formatter);
                                //CONVERT TIME
                                Date TIME = parser.parse(splitSt2[1]);

                                System.out.println("--------------------------------");
                                System.out.println("ScannerID : 1");
                                System.out.println("EmpID : " + splitSt[1].trim());
                                System.out.println("Date Time : " + date + "" + parser.format(TIME));

                                DatabaseConnection.Update("INSERT INTO Attendance (ScannerID, EmpID, Date, Time) VALUE ('" + scanner.getScannerID() + "', '" + splitSt[1].trim() + "', '" + date + "', '" + parser.format(TIME) + "')");

                                logcount++; //COUNT LOOP
                            }
                            System.out.println(">>>");
                        }

                    }
                }

                String msg = logcount + " Records downloaded successfully";

                if (logcount < 1) {
                    msg = "No records found.";
                }

                //ADD TO SCANNER TABLE
                DatabaseConnection.Update("Insert into ReadLog (Scanner, Date,Time, Description, User) "
                        + "Values ("
                        + "'" + scanner.getName() + " [ID: " + scanner.getScannerID() + "]" + "', "
                        + "'" + MainFrame.SysDate + "', "
                        + "'" + MainFrame.SysTime + "', "
                        + "'" + msg + "', "
                        + "'" + MainFrame.UserNow + "')");

                // delete the contents of the file
                PrintWriter writer = new PrintWriter(file);
                writer.print("");
                writer.close();
//                if (file.exists()) {
//                    file.delete();
//                }
            }

        } catch (Exception ex) {
//            JOptionPane.showMessageDialog(jdWorking, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            SaveErrors.SaveErrorLog(ex);
        }

        MainFrame.reading = false;
    }
}
