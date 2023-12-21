/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import Attendance.AttendanceCounts;
import Attendance.AttendanceTable;
import Attendance.DbAttendance;
import Employees.DbEmployees;
import Employees.Employee;
import Scanners.DbScanners;
import Scanners.ReadScanner;
import Scanners.Scanner;
import UI.Component.JtableCustomRendererEmp;
import UI.Component.JtableCustomRendererLog;
import UI.Component.JtableCustomRendererScanner;
import UI.Component.JtableCustomRendererUser;
import config.SystemSettings;
import database.DatabaseConnection;
import java.awt.CardLayout;
import java.awt.Color;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author harsh
 */
public class MainFrame extends javax.swing.JFrame {

    public static String SysTime = "";
    public static String SysDate = "";
    public static String UserNow = "";

    private CardLayout CardLayoutMain;
    private CardLayout CardLayoutSub;
    private CardLayout CardLayoutPopup;
    private CardLayout CardLayoutPopupScanner;

    private DbEmployees dbEmployee = new DbEmployees();
    private DbScanners dbScanner = new DbScanners();

    private int SecondsCount = 0;
    private int MinutesCount = 0;
    private int ReadEveryMinutes = 5; //5min (default)
    private int ReadEverySeconds = 60; //30sec (default)
    private boolean Enable_MinutesMode = false;

    private ReadScanner readScanner = new ReadScanner();
    private DbAttendance dbAttendance = new DbAttendance();

    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        initComponents();
        setTitle("Freidea Attendance Monitor");
        MainFrame.jlLoading.setVisible(false);

        Date d1 = new Date();
        dateChooser1.setDate(d1);

        jLabel17.setText(SystemSettings.Version_Number + " - " + SystemSettings.Build_Date);
        MainFrame.txtStatus.setText("<html><span style=\"font-size: 12pt; font-weight: bold\">Attendance Monitor -&nbsp;</span><span style=\"font-size: 11pt\">Copyright © 2023 Interithm</span></html>");

        SimpleDateFormat sdf2 = new SimpleDateFormat("h:mm a");
        SimpleDateFormat sdf4 = new SimpleDateFormat("EEEE, MMMMM d");

        SimpleDateFormat DbSdfTime = new SimpleDateFormat("HH:mm");
        SimpleDateFormat DbSdfDate = new SimpleDateFormat("yyyy-MM-dd");

        //<editor-fold defaultstate="collapsed" desc="Set Time">
        new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    
                    Date d = new Date();

                    String sTime = sdf2.format(d);
                    String fDate = sdf4.format(d);

                    jlStatusDateTime.setText(fDate + "  " + sTime + "  ");

                    SysTime = DbSdfTime.format(d);
                    SysDate = DbSdfDate.format(d);

                    if(reading){
                        continue;
                    }
                    SecondsCount++;

                    if (Enable_MinutesMode) {
                        //System.out.println(":: MINUTES MODE");
                        if (SecondsCount == 60) {
                            //System.out.println("60 sec");
                            SecondsCount = 0;
                            MinutesCount++;
                            if (MinutesCount == ReadEveryMinutes) {
                                //System.out.println("START READIING");
                                MinutesCount = 0;
                                ReadScanners();
                            }
                        }

                    } else {
                        
                        if (SecondsCount == ReadEverySeconds) {
                            System.out.println(":: SECONDS MODE");
                            System.out.println(":: START READIING");
                            SecondsCount = 0;
                            ReadScanners();
                        }
                    }

                    try {
                        Thread.sleep(1000); //waint 1sec
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }
        }).start();
        //</editor-fold>

        CardLayoutMain = (CardLayout) (jpMain.getLayout());
        CardLayoutSub = (CardLayout) (MainPanel.getLayout());
        CardLayoutPopup = (CardLayout) (jpAddCategory.getLayout());
        CardLayoutPopupScanner = (CardLayout) (jpAddScanner.getLayout());

        setMainUiPanel("EMP_LOGGING");

        this.setIconImage(new javax.swing.ImageIcon(getClass().getResource("/images/logo.png")).getImage());
        this.setExtendedState(MAXIMIZED_BOTH);

        TableColumn column = jtableEmployeeList.getColumnModel().getColumn(0);
        column.setCellRenderer(new JtableCustomRendererEmp());

        TableColumn column1 = jtableAttendance.getColumnModel().getColumn(0);
        column1.setCellRenderer(new JtableCustomRendererEmp());

        TableColumn column2 = jtableScannerList.getColumnModel().getColumn(0);
        column2.setCellRenderer(new JtableCustomRendererScanner());

        TableColumn column3 = jtableReadLog.getColumnModel().getColumn(0);
        column3.setCellRenderer(new JtableCustomRendererLog());

        TableColumn column4 = jtableUsers.getColumnModel().getColumn(0);
        column4.setCellRenderer(new JtableCustomRendererUser());

        loadEmployeeList();
        loadScannerList();
        loadDashboardData();
        loadAttTabledData();
        loadReadLog();

        loadUserList();
    }

    public static boolean reading = false;
    private static boolean Reading = false;
    private void ReadScanners() {
        System.out.println("READING NOW...");
        MainFrame.txtStatus.setText("Reading All Scanners..");
        MainFrame.jlLoading.setVisible(true);
        MainFrame.hbtnRead.setEnabled(false);

        reading = true;
        readScanner.ReadAllScanners();
        Reading = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (reading) {

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                System.out.println("END SCANNER READING");
                System.out.println("START REFRESH UI");
                loadDashboardData();
                loadAttTabledData();
                loadReadLog();
                Reading = false;

                MainFrame.txtStatus.setText("<html><span style=\"font-size: 12pt; font-weight: bold\">Attendance Monitor -&nbsp;</span><span style=\"font-size: 11pt\">Copyright © 2023 Interithm</span></html>");
                MainFrame.jlLoading.setVisible(false);
                MainFrame.hbtnRead.setEnabled(true);
                hbtnRead.setForeground(new java.awt.Color(252, 252, 252));
            }
        }).start();

    }

    public void RefreshAllData() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                loadDashboardData();
                loadAttTabledData();
                loadReadLog();

                MainFrame.txtStatus.setText("<html><span style=\"font-size: 12pt; font-weight: bold\">Attendance Monitor -&nbsp;</span><span style=\"font-size: 11pt\">Copyright © 2023 Interithm</span></html>");
                MainFrame.jlLoading.setVisible(false);
                MainFrame.hbtnRead.setEnabled(true);
                hbtnRead.setForeground(new java.awt.Color(252, 252, 252));

            }
        }).start();

    }

    //<editor-fold defaultstate="collapsed" desc="Change Panels">
    private void setMainUiPanel(String Panel) {
        if (Panel.equals("EMP_LOGGING")) {
            CardLayoutMain.show(jpMain, "EMP_LOGGING");
            lblMsg.setVisible(false);

        } else {
            CardLayoutMain.show(jpMain, "JOB_SECTION");
            btnAttendance.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 4, 0, 0, new java.awt.Color(0, 255, 250)));
            btnEmployees.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 4, 0, 0));
            btnSettings.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 4, 0, 0));
        }
    }

    private void setSubSectionUiPanel(String Panel) {
        if (Panel.equals("Attendance")) {
            CardLayoutSub.show(MainPanel, "Attendance");
            btnAttendance.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 4, 0, 0, new java.awt.Color(0, 255, 250)));
            btnEmployees.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 4, 0, 0));
            btnSettings.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 4, 0, 0));

        } else if (Panel.equals("Employees")) {
            CardLayoutSub.show(MainPanel, "Employees");
            btnAttendance.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 4, 0, 0));
            btnEmployees.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 4, 0, 0, new java.awt.Color(0, 255, 250)));
            btnSettings.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 4, 0, 0));

        } else if (Panel.equals("Settings")) {
            CardLayoutSub.show(MainPanel, "Settings");

            btnAttendance.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 4, 0, 0));
            btnEmployees.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 4, 0, 0));
            btnSettings.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 4, 0, 0, new java.awt.Color(0, 255, 250)));

        }
    }
    //</editor-fold>

    String EmpIDForEdit = "";

    //<editor-fold defaultstate="collapsed" desc="---- Show employee add edit popup window ----">
    private void showAddNewEmp(boolean edit, String empId) {

        jlErrorAddNewGroup.setVisible(false);
        jdAddNewEmp.getRootPane().setOpaque(false);
        jdAddNewEmp.getContentPane().setBackground(new Color(0, 0, 0, 0));
        jdAddNewEmp.setBackground(new Color(0, 0, 0, 0));
        jdAddNewEmp.pack();
        jdAddNewEmp.setSize(435, 532);
        jdAddNewEmp.setLocationRelativeTo(Splash.f1);
        EmpIDForEdit = empId;

        CardLayoutPopup.show(jpAddCategory, "AddEditEmp");

        if (!edit) {
            hbtnRound5.setVisible(false);
            txtAddNewEmpID.setEditable(true);
            txtAddNewEmpID.requestFocus();
            jmMsgTitle1.setText("Add New Employee");

            txtAddNewEmpID.setText("");
            txtScannerId.setText("");
            txtEmpName.setText("");
            txtEmpPhone.setText("");
            txtEmpEmail.setText("");
            jcmbPermanentStatus.setSelectedIndex(0);

        } else {
            hbtnRound5.setVisible(true);
            txtAddNewEmpID.setEditable(false);
            txtScannerId.requestFocus();
            txtAddNewEmpID.setText(empId);
            jmMsgTitle1.setText("Edit Employee");

            try {
                ResultSet r = DatabaseConnection.Search("Select * FROM Employees Where EmpID ='" + empId + "' LIMIT 1");
                while (r.next()) {
                    txtScannerId.setText(r.getString("ScannerId"));
                    txtEmpName.setText(r.getString("Name"));
                    txtEmpPhone.setText(r.getString("Phone"));
                    txtEmpEmail.setText(r.getString("Email"));
                    jcmbPermanentStatus.setSelectedItem(r.getString("PermanentStatus"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        jdAddNewEmp.setVisible(true);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="---- Show employee add edit popup window ----">
    private void showAddNewScannerDevice(boolean edit, String ScannerID) {

        jlErrorAddNewScanner.setVisible(false);
        jdAddScanner.getRootPane().setOpaque(false);
        jdAddScanner.getContentPane().setBackground(new Color(0, 0, 0, 0));
        jdAddScanner.setBackground(new Color(0, 0, 0, 0));
        jdAddScanner.pack();
        jdAddScanner.setSize(435, 532);
        jdAddScanner.setLocationRelativeTo(Splash.f1);

        CardLayoutPopupScanner.show(jpAddScanner, "AddEditEmp");
        txtScannerName.requestFocus();

        if (!edit) {
            btnDeleteScanner.setVisible(false);
            jmMsgTitle2.setText("Add New Scanner Device");

            txtScannerName.setText("");
            txtScannerIP.setText("");
            txtScannerPort.setText("");
            txtScannerNote.setText("");

            int latestID = 0;
            try {
                ResultSet r = DatabaseConnection.Search("Select ID FROM Scanners Order By ID DESC LIMIT 1");
                while (r.next()) {

                    latestID = r.getInt("ID");
                }

            } catch (Exception e) {
                latestID = 0;
            }

            latestID++;
            txtScannerID.setText(latestID + "");

        } else {
            btnDeleteScanner.setVisible(true);

            txtAddNewEmpID.setText(ScannerID);
            jmMsgTitle2.setText("Edit Scanner Device");

            txtScannerID.setText(ScannerID);

            try {
                ResultSet r = DatabaseConnection.Search("Select * FROM Scanners Where ID ='" + ScannerID + "' LIMIT 1");
                while (r.next()) {

                    txtScannerName.setText(r.getString("Scanner_Name"));
                    txtScannerIP.setText(r.getString("IP_Address"));
                    txtScannerPort.setText(r.getString("Port"));
                    txtScannerNote.setText(r.getString("Note"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        jdAddScanner.setVisible(true);
    }
    //</editor-fold> 

    //<editor-fold defaultstate="collapsed" desc="Load Emp List">
    private void loadEmployeeList() {

        try {
            String SearchString = txtEmpSearch.getText().replaceAll("'", "''");
            String PerStatus = jComboBox1.getSelectedItem().toString();

            DefaultTableModel df = (DefaultTableModel) jtableEmployeeList.getModel();
            df.setRowCount(0);

            ResultSet r;
            if (PerStatus.equals("<html><b>All</b></html>")) {
                r = DatabaseConnection.Search("SELECT * FROM Employees WHERE EmpId Like '%" + SearchString + "%' OR Name Like '%" + SearchString + "%' ");
            } else {
                r = DatabaseConnection.Search("SELECT * FROM Employees WHERE (EmpId Like '%" + SearchString + "%' OR Name Like '%" + SearchString + "%') And PermanentStatus = '" + PerStatus + "' ");
            }

            while (r.next()) {
                Vector v = new Vector();

                String EmpID = r.getString("EmpId");
                if (!EmpID.isEmpty()) {
                    if (EmpID.toLowerCase().contains(SearchString.toLowerCase())) {
                        String OutPut = "<html>" + EmpID.replaceFirst("(?i)" + Pattern.quote(SearchString), "<span style=\"background-color: #E3F602 ;\">$0</span>") + "</html>";
                        v.add(OutPut);
                    } else {
                        v.add(EmpID);
                    }
                }

                String Name = r.getString("Name");

                if (!Name.isEmpty()) {
                    if (Name.toLowerCase().contains(SearchString.toLowerCase())) {
                        String OutPut = "<html>" + Name.replaceFirst("(?i)" + Pattern.quote(SearchString), "<span style=\"background-color: #E3F602 ;\">$0</span>") + "</html>";
                        v.add(OutPut);
                    } else {
                        v.add(Name);
                    }
                } else {
                    v.add(Name);
                }

                v.add(r.getString("Phone"));
                v.add(r.getString("Email"));
                v.add(r.getString("PermanentStatus"));

                df.addRow(v);

            }

        } catch (Exception e) {
        }

    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Load Scanners List">
    private void loadScannerList() {

        try {

            DefaultTableModel df = (DefaultTableModel) jtableScannerList.getModel();
            df.setRowCount(0);

            ResultSet r = DatabaseConnection.Search("SELECT * FROM Scanners Order by ID ");

            while (r.next()) {
                Vector v = new Vector();

                v.add(r.getString("ID"));
                v.add(r.getString("Scanner_Name"));
                v.add(r.getString("IP_Address"));
                v.add(r.getString("Port"));
                v.add(r.getString("Note"));

                df.addRow(v);

            }

        } catch (Exception e) {
        }

    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Load Readlog">
    public void loadReadLog() {

        try {

            SimpleDateFormat DbSdfTime = new SimpleDateFormat("HH:mm");
            SimpleDateFormat DbSdfDate = new SimpleDateFormat("yyyy-MM-dd");

            SimpleDateFormat sdf4 = new SimpleDateFormat("EEEE, MMMMM d");
            SimpleDateFormat sdf2 = new SimpleDateFormat("h:mm a");

            Date today = new Date();
            Calendar cal = new GregorianCalendar();
            cal.setTime(today);

            Date dateStart = cal.getTime();
            cal.add(Calendar.DAY_OF_MONTH, -30);
            Date dateEnd = cal.getTime();

            String strDateStart = DbSdfDate.format(dateStart);
            String strDateEnd = DbSdfDate.format(dateEnd);

            DefaultTableModel df = (DefaultTableModel) jtableReadLog.getModel();
            df.setRowCount(0);

            ResultSet r = DatabaseConnection.Search("SELECT * FROM ReadLog Where Date Between '" + strDateStart + "' And '" + dateEnd + "'  Order by ID ");

            while (r.next()) {
                Vector v = new Vector();

                v.add(r.getString("Scanner"));
                v.add(sdf4.format(DbSdfDate.parse(r.getString("Date"))));
                v.add(sdf2.format(DbSdfTime.parse(r.getString("Time"))));
                v.add(r.getString("Description"));

                df.addRow(v);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Load Dashboard Data">
    public void loadDashboardData() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = sdf.format(dateChooser1.getDate());

        AttendanceCounts attCount = dbAttendance.loadAttenanceCount(strDate);

        jlTotalIn.setText(attCount.getTotalIn() + "");
        jlTotalOut.setText(attCount.getTotalOut() + "");

        jlPerIn.setText(attCount.getPermanentIn() + "");
        jlPerOut.setText(attCount.getPermanentOut() + "");

        jlNonPerIn.setText(attCount.getNonPermanentIn() + "");
        jlNonPerOut.setText(attCount.getNonPermanentOut() + "");

        jlPresentPer.setText(attCount.getPresentPermanent() + "");
        jlPresentNonPer.setText(attCount.getPresentNonPermanent() + "");
        jlPresentTotal.setText(attCount.getPresentTotal() + "");

        jlEmpTotal.setText(attCount.getPresentTotal() + " out of " + attCount.getTotalEmps()
                + " employees were present today. "
                + attCount.getTotalOut()
                + " employee(s) were away for a visit outside the office.");

        jlAbsentPer.setText(attCount.getAbsentPermanent() + "");
        jlAbsentNonPer.setText(attCount.getAbsentNonPermanent() + "");
        jlAbsentTotal.setText(attCount.getAbsentTotal() + "");

    }

    public void loadAttTabledData() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = sdf.format(dateChooser1.getDate());
        ArrayList<AttendanceTable> attTable = dbAttendance.GetAttTableData(strDate,
                hTextField1.getText(),
                jComboBox4.getSelectedItem().toString(),
                jComboBox3.getSelectedItem().toString());

        DefaultTableModel df = (DefaultTableModel) jtableAttendance.getModel();
        df.setRowCount(0);

        for (AttendanceTable attendanceTable : attTable) {
            Vector v = new Vector();

            v.add(attendanceTable.getEmpID());
            v.add(attendanceTable.getName());
            v.add(attendanceTable.getStatus());

            df.addRow(v);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Load User List">
    private void loadUserList() {

        try {

            DefaultTableModel df = (DefaultTableModel) jtableUsers.getModel();
            df.setRowCount(0);

            ResultSet r = DatabaseConnection.Search("SELECT * FROM User Order by UserId ");

            while (r.next()) {
                Vector v = new Vector();

                v.add(r.getString("UserId"));
                v.add(r.getString("Username"));
                v.add(r.getString("DisplayName"));
                v.add(r.getString("Description"));

                df.addRow(v);

            }

        } catch (Exception e) {
        }

    }
//</editor-fold>

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jdAddNewEmp = new javax.swing.JDialog();
        shadowPanel1 = new NEW.ShadowPanel();
        jpAddCategory = new javax.swing.JPanel();
        AddEditEmp = new javax.swing.JPanel();
        txtScannerId = new javax.swing.JTextField();
        jLabel69 = new javax.swing.JLabel();
        jmMsgTitle1 = new javax.swing.JLabel();
        txtAddNewEmpID = new javax.swing.JTextField();
        jLabel41 = new javax.swing.JLabel();
        jlErrorAddNewGroup = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        txtEmpName = new javax.swing.JTextField();
        jLabel43 = new javax.swing.JLabel();
        txtEmpPhone = new javax.swing.JTextField();
        jLabel44 = new javax.swing.JLabel();
        txtEmpEmail = new javax.swing.JTextField();
        jLabel45 = new javax.swing.JLabel();
        jcmbPermanentStatus = new javax.swing.JComboBox<>();
        hbtnRound4 = new hbtn.HbtnRound();
        hbtnRound5 = new hbtn.HbtnRound();
        hbtnRound6 = new hbtn.HbtnRound();
        DeleteEmp = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        hbtnRound7 = new hbtn.HbtnRound();
        hbtnRound8 = new hbtn.HbtnRound();
        jdAddScanner = new javax.swing.JDialog();
        shadowPanel2 = new NEW.ShadowPanel();
        jpAddScanner = new javax.swing.JPanel();
        AddEditEmp1 = new javax.swing.JPanel();
        txtScannerNote = new javax.swing.JTextField();
        jmMsgTitle2 = new javax.swing.JLabel();
        txtScannerID = new javax.swing.JTextField();
        jLabel71 = new javax.swing.JLabel();
        jlErrorAddNewScanner = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        txtScannerName = new javax.swing.JTextField();
        jLabel73 = new javax.swing.JLabel();
        txtScannerIP = new javax.swing.JTextField();
        jLabel74 = new javax.swing.JLabel();
        txtScannerPort = new javax.swing.JTextField();
        jLabel75 = new javax.swing.JLabel();
        hbtnRound14 = new hbtn.HbtnRound();
        btnDeleteScanner = new hbtn.HbtnRound();
        hbtnRound16 = new hbtn.HbtnRound();
        DeleteEmp1 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        hbtnRound17 = new hbtn.HbtnRound();
        hbtnRound18 = new hbtn.HbtnRound();
        jpMain = new javax.swing.JPanel();
        Locked = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        shadowPanelOriginal2 = new NEW.ShadowPanelOriginal();
        jPanel11 = new javax.swing.JPanel();
        lblMsg = new javax.swing.JLabel();
        tfPassword = new javax.swing.JPasswordField();
        jLabel39 = new javax.swing.JLabel();
        tfUserName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        hbtnRound1 = new hbtn.HbtnRound();
        jLabel1 = new javax.swing.JLabel();
        MainSection = new javax.swing.JPanel();
        navigation = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        hbtnRound10 = new hbtn.HbtnRound();
        btnSettings = new hbtn.HbtnRound();
        btnEmployees = new hbtn.HbtnRound();
        btnAttendance = new hbtn.HbtnRound();
        MainPanel = new javax.swing.JPanel();
        Attendance = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        shadowPanelOriginal1 = new NEW.ShadowPanelOriginal();
        jLabel9 = new javax.swing.JLabel();
        jlTotalIn = new javax.swing.JLabel();
        jlTotalOut = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        shadowPanelOriginal7 = new NEW.ShadowPanelOriginal();
        jLabel40 = new javax.swing.JLabel();
        jlPerIn = new javax.swing.JLabel();
        jlPerOut = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        shadowPanelOriginal8 = new NEW.ShadowPanelOriginal();
        jLabel50 = new javax.swing.JLabel();
        jlNonPerIn = new javax.swing.JLabel();
        jlNonPerOut = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        shadowPanelOriginal9 = new NEW.ShadowPanelOriginal();
        jLabel55 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        jlPresentPer = new javax.swing.JLabel();
        jlPresentNonPer = new javax.swing.JLabel();
        jlPresentTotal = new javax.swing.JLabel();
        shadowPanelOriginal10 = new NEW.ShadowPanelOriginal();
        jLabel62 = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        jlAbsentPer = new javax.swing.JLabel();
        jlAbsentNonPer = new javax.swing.JLabel();
        jlAbsentTotal = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jComboBox3 = new javax.swing.JComboBox<>();
        dateChooser1 = new NEW.DateChooser();
        hTextField1 = new NEW.hTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtableAttendance = new javax.swing.JTable();
        jComboBox4 = new javax.swing.JComboBox<>();
        jPanel6 = new javax.swing.JPanel();
        shadowPanelOriginal6 = new NEW.ShadowPanelOriginal();
        jPanel7 = new javax.swing.JPanel();
        jlEmpTotal = new javax.swing.JLabel();
        pnlEmployees = new javax.swing.JPanel();
        txtEmpSearch = new NEW.hTextField();
        jScrollPane4 = new javax.swing.JScrollPane();
        jtableEmployeeList = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        hbtnRound2 = new hbtn.HbtnRound();
        hbtnRound3 = new hbtn.HbtnRound();
        jComboBox1 = new javax.swing.JComboBox<>();
        Settings = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel10 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jtableReadLog = new javax.swing.JTable();
        hbtnRead = new hbtn.HbtnRound();
        jlLoading = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jtableScannerList = new javax.swing.JTable();
        jLabel15 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        hbtnRound9 = new hbtn.HbtnRound();
        hbtnRound11 = new hbtn.HbtnRound();
        jPanel14 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jtableUsers = new javax.swing.JTable();
        jLabel20 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        hbtnRound12 = new hbtn.HbtnRound();
        hbtnRound13 = new hbtn.HbtnRound();
        jPanel12 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jpStatusBar = new javax.swing.JPanel();
        txtStatus = new javax.swing.JLabel();
        jlStatusDateTime = new javax.swing.JLabel();

        jdAddNewEmp.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jdAddNewEmp.setTitle("More Details");
        jdAddNewEmp.setModalExclusionType(java.awt.Dialog.ModalExclusionType.TOOLKIT_EXCLUDE);
        jdAddNewEmp.setModalityType(java.awt.Dialog.ModalityType.TOOLKIT_MODAL);
        jdAddNewEmp.setUndecorated(true);
        jdAddNewEmp.setResizable(false);
        jdAddNewEmp.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jdAddNewEmpComponentShown(evt);
            }
        });

        jpAddCategory.setBackground(new java.awt.Color(255, 255, 255));
        jpAddCategory.setLayout(new java.awt.CardLayout());

        AddEditEmp.setBackground(new java.awt.Color(255, 255, 255));
        AddEditEmp.setLayout(null);

        txtScannerId.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        txtScannerId.setForeground(new java.awt.Color(102, 102, 102));
        AddEditEmp.add(txtScannerId);
        txtScannerId.setBounds(190, 80, 160, 30);

        jLabel69.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        jLabel69.setForeground(new java.awt.Color(102, 102, 102));
        jLabel69.setText("Scanner ID");
        AddEditEmp.add(jLabel69);
        jLabel69.setBounds(190, 50, 110, 30);

        jmMsgTitle1.setFont(new java.awt.Font("Roboto", 1, 16)); // NOI18N
        jmMsgTitle1.setForeground(new java.awt.Color(51, 153, 255));
        jmMsgTitle1.setText("Add New Employee");
        AddEditEmp.add(jmMsgTitle1);
        jmMsgTitle1.setBounds(0, 0, 160, 30);

        txtAddNewEmpID.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        txtAddNewEmpID.setForeground(new java.awt.Color(102, 102, 102));
        AddEditEmp.add(txtAddNewEmpID);
        txtAddNewEmpID.setBounds(0, 80, 170, 30);

        jLabel41.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        jLabel41.setForeground(new java.awt.Color(102, 102, 102));
        jLabel41.setText("Emp ID");
        AddEditEmp.add(jLabel41);
        jLabel41.setBounds(0, 50, 110, 30);

        jlErrorAddNewGroup.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        jlErrorAddNewGroup.setForeground(new java.awt.Color(153, 0, 0));
        jlErrorAddNewGroup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/error_icon_18.png"))); // NOI18N
        jlErrorAddNewGroup.setText("This is the error message");
        AddEditEmp.add(jlErrorAddNewGroup);
        jlErrorAddNewGroup.setBounds(0, 360, 350, 30);

        jLabel42.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        jLabel42.setForeground(new java.awt.Color(102, 102, 102));
        jLabel42.setText("Name");
        AddEditEmp.add(jLabel42);
        jLabel42.setBounds(0, 110, 110, 30);

        txtEmpName.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        txtEmpName.setForeground(new java.awt.Color(102, 102, 102));
        AddEditEmp.add(txtEmpName);
        txtEmpName.setBounds(0, 140, 350, 30);

        jLabel43.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        jLabel43.setForeground(new java.awt.Color(102, 102, 102));
        jLabel43.setText("Phone");
        AddEditEmp.add(jLabel43);
        jLabel43.setBounds(0, 170, 110, 30);

        txtEmpPhone.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        txtEmpPhone.setForeground(new java.awt.Color(102, 102, 102));
        AddEditEmp.add(txtEmpPhone);
        txtEmpPhone.setBounds(0, 200, 350, 30);

        jLabel44.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        jLabel44.setForeground(new java.awt.Color(102, 102, 102));
        jLabel44.setText("Email");
        AddEditEmp.add(jLabel44);
        jLabel44.setBounds(0, 230, 110, 30);

        txtEmpEmail.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        txtEmpEmail.setForeground(new java.awt.Color(102, 102, 102));
        AddEditEmp.add(txtEmpEmail);
        txtEmpEmail.setBounds(0, 260, 350, 30);

        jLabel45.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        jLabel45.setForeground(new java.awt.Color(102, 102, 102));
        jLabel45.setText("Permanent Status");
        AddEditEmp.add(jLabel45);
        jLabel45.setBounds(0, 290, 140, 30);

        jcmbPermanentStatus.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        jcmbPermanentStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Permanent", "Non-Permanent" }));
        AddEditEmp.add(jcmbPermanentStatus);
        jcmbPermanentStatus.setBounds(0, 320, 350, 30);

        hbtnRound4.setText("Save");
        hbtnRound4.setBackColor(new java.awt.Color(0, 0, 0));
        hbtnRound4.setBackColorMouseDown(new java.awt.Color(0, 0, 0));
        hbtnRound4.setBackColorMouseIn(new java.awt.Color(51, 51, 51));
        hbtnRound4.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        hbtnRound4.setText_Long("FP User Login");
        hbtnRound4.setText_Short("Login");
        hbtnRound4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hbtnRound4ActionPerformed(evt);
            }
        });
        AddEditEmp.add(hbtnRound4);
        hbtnRound4.setBounds(260, 410, 90, 36);

        hbtnRound5.setText("Delete");
        hbtnRound5.setBackColor(new java.awt.Color(255, 102, 102));
        hbtnRound5.setBackColorMouseDown(new java.awt.Color(225, 85, 85));
        hbtnRound5.setBackColorMouseIn(new java.awt.Color(253, 124, 124));
        hbtnRound5.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        hbtnRound5.setText_Long("FP User Login");
        hbtnRound5.setText_Short("Login");
        hbtnRound5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hbtnRound5ActionPerformed(evt);
            }
        });
        AddEditEmp.add(hbtnRound5);
        hbtnRound5.setBounds(250, 0, 100, 36);

        hbtnRound6.setText("Cancel");
        hbtnRound6.setBackColor(new java.awt.Color(0, 0, 0));
        hbtnRound6.setBackColorMouseDown(new java.awt.Color(0, 0, 0));
        hbtnRound6.setBackColorMouseIn(new java.awt.Color(51, 51, 51));
        hbtnRound6.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        hbtnRound6.setText_Long("FP User Login");
        hbtnRound6.setText_Short("Login");
        hbtnRound6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hbtnRound6ActionPerformed(evt);
            }
        });
        AddEditEmp.add(hbtnRound6);
        hbtnRound6.setBounds(160, 410, 90, 36);

        jpAddCategory.add(AddEditEmp, "AddEditEmp");

        DeleteEmp.setBackground(new java.awt.Color(255, 255, 255));

        jLabel6.setBackground(new java.awt.Color(51, 51, 51));
        jLabel6.setFont(new java.awt.Font("Roboto", 0, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(102, 102, 102));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Delete Employee?");
        jLabel6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel7.setBackground(new java.awt.Color(51, 51, 51));
        jLabel7.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(51, 51, 51));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("<html><center>Do you really want to delete this employee? This process cannot be undone.");
        jLabel7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/remove100.png"))); // NOI18N

        hbtnRound7.setText("Cancel");
        hbtnRound7.setBackColor(new java.awt.Color(102, 102, 102));
        hbtnRound7.setBackColorMouseDown(new java.awt.Color(94, 94, 94));
        hbtnRound7.setBackColorMouseIn(new java.awt.Color(121, 121, 121));
        hbtnRound7.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        hbtnRound7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hbtnRound7ActionPerformed(evt);
            }
        });

        hbtnRound8.setText("Delete");
        hbtnRound8.setBackColor(new java.awt.Color(255, 102, 102));
        hbtnRound8.setBackColorMouseDown(new java.awt.Color(225, 85, 85));
        hbtnRound8.setBackColorMouseIn(new java.awt.Color(253, 124, 124));
        hbtnRound8.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        hbtnRound8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hbtnRound8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout DeleteEmpLayout = new javax.swing.GroupLayout(DeleteEmp);
        DeleteEmp.setLayout(DeleteEmpLayout);
        DeleteEmpLayout.setHorizontalGroup(
            DeleteEmpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DeleteEmpLayout.createSequentialGroup()
                .addGroup(DeleteEmpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(DeleteEmpLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(DeleteEmpLayout.createSequentialGroup()
                        .addGap(123, 123, 123)
                        .addComponent(jLabel8)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(DeleteEmpLayout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addComponent(hbtnRound7, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(hbtnRound8, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(78, Short.MAX_VALUE))
        );
        DeleteEmpLayout.setVerticalGroup(
            DeleteEmpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DeleteEmpLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(jLabel8)
                .addGap(18, 18, 18)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(DeleteEmpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(hbtnRound8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(hbtnRound7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(137, Short.MAX_VALUE))
        );

        jpAddCategory.add(DeleteEmp, "DeleteEmp");

        javax.swing.GroupLayout shadowPanel1Layout = new javax.swing.GroupLayout(shadowPanel1);
        shadowPanel1.setLayout(shadowPanel1Layout);
        shadowPanel1Layout.setHorizontalGroup(
            shadowPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, shadowPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jpAddCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(642, 642, 642))
        );
        shadowPanel1Layout.setVerticalGroup(
            shadowPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(shadowPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jpAddCategory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jdAddNewEmpLayout = new javax.swing.GroupLayout(jdAddNewEmp.getContentPane());
        jdAddNewEmp.getContentPane().setLayout(jdAddNewEmpLayout);
        jdAddNewEmpLayout.setHorizontalGroup(
            jdAddNewEmpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(shadowPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 435, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jdAddNewEmpLayout.setVerticalGroup(
            jdAddNewEmpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(shadowPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 532, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jdAddScanner.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jdAddScanner.setTitle("More Details");
        jdAddScanner.setModalExclusionType(java.awt.Dialog.ModalExclusionType.TOOLKIT_EXCLUDE);
        jdAddScanner.setModalityType(java.awt.Dialog.ModalityType.TOOLKIT_MODAL);
        jdAddScanner.setUndecorated(true);
        jdAddScanner.setResizable(false);
        jdAddScanner.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jdAddScannerComponentShown(evt);
            }
        });

        jpAddScanner.setBackground(new java.awt.Color(255, 255, 255));
        jpAddScanner.setLayout(new java.awt.CardLayout());

        AddEditEmp1.setBackground(new java.awt.Color(255, 255, 255));
        AddEditEmp1.setLayout(null);

        txtScannerNote.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        txtScannerNote.setForeground(new java.awt.Color(102, 102, 102));
        AddEditEmp1.add(txtScannerNote);
        txtScannerNote.setBounds(0, 320, 350, 30);

        jmMsgTitle2.setFont(new java.awt.Font("Roboto", 1, 16)); // NOI18N
        jmMsgTitle2.setForeground(new java.awt.Color(51, 153, 255));
        jmMsgTitle2.setText("Add New Scanner Device");
        AddEditEmp1.add(jmMsgTitle2);
        jmMsgTitle2.setBounds(0, 0, 210, 30);

        txtScannerID.setEditable(false);
        txtScannerID.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        txtScannerID.setForeground(new java.awt.Color(102, 102, 102));
        AddEditEmp1.add(txtScannerID);
        txtScannerID.setBounds(0, 80, 170, 30);

        jLabel71.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        jLabel71.setForeground(new java.awt.Color(102, 102, 102));
        jLabel71.setText("Scanner ID");
        AddEditEmp1.add(jLabel71);
        jLabel71.setBounds(0, 50, 110, 30);

        jlErrorAddNewScanner.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        jlErrorAddNewScanner.setForeground(new java.awt.Color(153, 0, 0));
        jlErrorAddNewScanner.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/error_icon_18.png"))); // NOI18N
        jlErrorAddNewScanner.setText("This is the error message");
        AddEditEmp1.add(jlErrorAddNewScanner);
        jlErrorAddNewScanner.setBounds(0, 360, 350, 30);

        jLabel72.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        jLabel72.setForeground(new java.awt.Color(102, 102, 102));
        jLabel72.setText("Scanner Name");
        AddEditEmp1.add(jLabel72);
        jLabel72.setBounds(0, 110, 110, 30);

        txtScannerName.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        txtScannerName.setForeground(new java.awt.Color(102, 102, 102));
        AddEditEmp1.add(txtScannerName);
        txtScannerName.setBounds(0, 140, 350, 30);

        jLabel73.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        jLabel73.setForeground(new java.awt.Color(102, 102, 102));
        jLabel73.setText("IP Address");
        AddEditEmp1.add(jLabel73);
        jLabel73.setBounds(0, 170, 110, 30);

        txtScannerIP.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        txtScannerIP.setForeground(new java.awt.Color(102, 102, 102));
        AddEditEmp1.add(txtScannerIP);
        txtScannerIP.setBounds(0, 200, 350, 30);

        jLabel74.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        jLabel74.setForeground(new java.awt.Color(102, 102, 102));
        jLabel74.setText("Port Number");
        AddEditEmp1.add(jLabel74);
        jLabel74.setBounds(0, 230, 110, 30);

        txtScannerPort.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        txtScannerPort.setForeground(new java.awt.Color(102, 102, 102));
        AddEditEmp1.add(txtScannerPort);
        txtScannerPort.setBounds(0, 260, 350, 30);

        jLabel75.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        jLabel75.setForeground(new java.awt.Color(102, 102, 102));
        jLabel75.setText("Note");
        AddEditEmp1.add(jLabel75);
        jLabel75.setBounds(0, 290, 140, 30);

        hbtnRound14.setText("Save");
        hbtnRound14.setBackColor(new java.awt.Color(0, 0, 0));
        hbtnRound14.setBackColorMouseDown(new java.awt.Color(0, 0, 0));
        hbtnRound14.setBackColorMouseIn(new java.awt.Color(51, 51, 51));
        hbtnRound14.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        hbtnRound14.setText_Long("FP User Login");
        hbtnRound14.setText_Short("Login");
        hbtnRound14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hbtnRound14ActionPerformed(evt);
            }
        });
        AddEditEmp1.add(hbtnRound14);
        hbtnRound14.setBounds(260, 410, 90, 36);

        btnDeleteScanner.setText("Delete");
        btnDeleteScanner.setBackColor(new java.awt.Color(255, 102, 102));
        btnDeleteScanner.setBackColorMouseDown(new java.awt.Color(225, 85, 85));
        btnDeleteScanner.setBackColorMouseIn(new java.awt.Color(253, 124, 124));
        btnDeleteScanner.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        btnDeleteScanner.setText_Long("FP User Login");
        btnDeleteScanner.setText_Short("Login");
        btnDeleteScanner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteScannerActionPerformed(evt);
            }
        });
        AddEditEmp1.add(btnDeleteScanner);
        btnDeleteScanner.setBounds(250, 0, 100, 36);

        hbtnRound16.setText("Cancel");
        hbtnRound16.setBackColor(new java.awt.Color(0, 0, 0));
        hbtnRound16.setBackColorMouseDown(new java.awt.Color(0, 0, 0));
        hbtnRound16.setBackColorMouseIn(new java.awt.Color(51, 51, 51));
        hbtnRound16.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        hbtnRound16.setText_Long("FP User Login");
        hbtnRound16.setText_Short("Login");
        hbtnRound16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hbtnRound16ActionPerformed(evt);
            }
        });
        AddEditEmp1.add(hbtnRound16);
        hbtnRound16.setBounds(160, 410, 90, 36);

        jpAddScanner.add(AddEditEmp1, "AddEditEmp");

        DeleteEmp1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel21.setBackground(new java.awt.Color(51, 51, 51));
        jLabel21.setFont(new java.awt.Font("Roboto", 0, 24)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(102, 102, 102));
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("Delete Scanner?");
        jLabel21.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel22.setBackground(new java.awt.Color(51, 51, 51));
        jLabel22.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(51, 51, 51));
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("<html><center>Do you really want to delete this scanner device? This process cannot be undone.");
        jLabel22.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/remove100.png"))); // NOI18N

        hbtnRound17.setText("Cancel");
        hbtnRound17.setBackColor(new java.awt.Color(102, 102, 102));
        hbtnRound17.setBackColorMouseDown(new java.awt.Color(94, 94, 94));
        hbtnRound17.setBackColorMouseIn(new java.awt.Color(121, 121, 121));
        hbtnRound17.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        hbtnRound17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hbtnRound17ActionPerformed(evt);
            }
        });

        hbtnRound18.setText("Delete");
        hbtnRound18.setBackColor(new java.awt.Color(255, 102, 102));
        hbtnRound18.setBackColorMouseDown(new java.awt.Color(225, 85, 85));
        hbtnRound18.setBackColorMouseIn(new java.awt.Color(253, 124, 124));
        hbtnRound18.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        hbtnRound18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hbtnRound18ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout DeleteEmp1Layout = new javax.swing.GroupLayout(DeleteEmp1);
        DeleteEmp1.setLayout(DeleteEmp1Layout);
        DeleteEmp1Layout.setHorizontalGroup(
            DeleteEmp1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DeleteEmp1Layout.createSequentialGroup()
                .addGroup(DeleteEmp1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(DeleteEmp1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(DeleteEmp1Layout.createSequentialGroup()
                        .addGap(123, 123, 123)
                        .addComponent(jLabel23)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(DeleteEmp1Layout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addComponent(hbtnRound17, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(hbtnRound18, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(78, Short.MAX_VALUE))
        );
        DeleteEmp1Layout.setVerticalGroup(
            DeleteEmp1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DeleteEmp1Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(jLabel23)
                .addGap(18, 18, 18)
                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(DeleteEmp1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(hbtnRound18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(hbtnRound17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(137, Short.MAX_VALUE))
        );

        jpAddScanner.add(DeleteEmp1, "DeleteEmp");

        javax.swing.GroupLayout shadowPanel2Layout = new javax.swing.GroupLayout(shadowPanel2);
        shadowPanel2.setLayout(shadowPanel2Layout);
        shadowPanel2Layout.setHorizontalGroup(
            shadowPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, shadowPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jpAddScanner, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(642, 642, 642))
        );
        shadowPanel2Layout.setVerticalGroup(
            shadowPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(shadowPanel2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jpAddScanner, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jdAddScannerLayout = new javax.swing.GroupLayout(jdAddScanner.getContentPane());
        jdAddScanner.getContentPane().setLayout(jdAddScannerLayout);
        jdAddScannerLayout.setHorizontalGroup(
            jdAddScannerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(shadowPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 435, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jdAddScannerLayout.setVerticalGroup(
            jdAddScannerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(shadowPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 532, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Attendance Reader");
        setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N

        jpMain.setLayout(new java.awt.CardLayout());

        Locked.setBackground(new java.awt.Color(179, 209, 214));

        jPanel1.setOpaque(false);

        jLabel3.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 192, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Attendance Monitor");

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/calendar_100.png"))); // NOI18N
        jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        shadowPanelOriginal2.setBackground(new java.awt.Color(255, 255, 255));

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));
        jPanel11.setLayout(null);

        lblMsg.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        lblMsg.setForeground(new java.awt.Color(153, 0, 51));
        lblMsg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Error_20.png"))); // NOI18N
        lblMsg.setText("Invalid Number");
        lblMsg.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 204, 204)));
        lblMsg.setIconTextGap(8);
        jPanel11.add(lblMsg);
        lblMsg.setBounds(50, 14, 270, 30);

        tfPassword.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tfPassword.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tfPasswordKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tfPasswordKeyReleased(evt);
            }
        });
        jPanel11.add(tfPassword);
        tfPassword.setBounds(50, 130, 270, 30);

        jLabel39.setFont(new java.awt.Font("Roboto", 1, 11)); // NOI18N
        jLabel39.setForeground(new java.awt.Color(13, 138, 198));
        jLabel39.setText("USERNAME");
        jPanel11.add(jLabel39);
        jLabel39.setBounds(50, 50, 90, 20);

        tfUserName.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        tfUserName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tfUserNameKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tfUserNameKeyReleased(evt);
            }
        });
        jPanel11.add(tfUserName);
        tfUserName.setBounds(50, 70, 270, 30);

        jLabel2.setFont(new java.awt.Font("Roboto", 1, 11)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(13, 138, 198));
        jLabel2.setText("PASSWORD");
        jPanel11.add(jLabel2);
        jLabel2.setBounds(50, 110, 70, 20);

        hbtnRound1.setText("Login");
        hbtnRound1.setBackColor(new java.awt.Color(0, 0, 0));
        hbtnRound1.setBackColorMouseDown(new java.awt.Color(0, 0, 0));
        hbtnRound1.setBackColorMouseIn(new java.awt.Color(51, 51, 51));
        hbtnRound1.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        hbtnRound1.setText_Long("FP User Login");
        hbtnRound1.setText_Short("Login");
        hbtnRound1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hbtnRound1ActionPerformed(evt);
            }
        });
        jPanel11.add(hbtnRound1);
        hbtnRound1.setBounds(240, 170, 80, 37);

        javax.swing.GroupLayout shadowPanelOriginal2Layout = new javax.swing.GroupLayout(shadowPanelOriginal2);
        shadowPanelOriginal2.setLayout(shadowPanelOriginal2Layout);
        shadowPanelOriginal2Layout.setHorizontalGroup(
            shadowPanelOriginal2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(shadowPanelOriginal2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        shadowPanelOriginal2Layout.setVerticalGroup(
            shadowPanelOriginal2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(shadowPanelOriginal2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(shadowPanelOriginal2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addGap(0, 0, 0)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(shadowPanelOriginal2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel1.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(51, 51, 51));
        jLabel1.setText("Copyright © Interithm. All rights reserved.");

        javax.swing.GroupLayout LockedLayout = new javax.swing.GroupLayout(Locked);
        Locked.setLayout(LockedLayout);
        LockedLayout.setHorizontalGroup(
            LockedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, LockedLayout.createSequentialGroup()
                .addContainerGap(334, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(334, Short.MAX_VALUE))
            .addGroup(LockedLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        LockedLayout.setVerticalGroup(
            LockedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, LockedLayout.createSequentialGroup()
                .addContainerGap(82, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 79, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addContainerGap())
        );

        jpMain.add(Locked, "EMP_LOGGING");

        MainSection.setBackground(new java.awt.Color(204, 204, 204));

        navigation.setBackground(new java.awt.Color(51, 51, 51));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 219, 239));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/logo.png"))); // NOI18N
        jLabel5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        hbtnRound10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/shutdown_40.png"))); // NOI18N
        hbtnRound10.setBackColor(new java.awt.Color(51, 51, 51));
        hbtnRound10.setBackColorMouseDown(new java.awt.Color(0, 0, 0));
        hbtnRound10.setBackColorMouseIn(new java.awt.Color(71, 71, 71));
        hbtnRound10.setBorderMode(hbtn.HbtnRound.Borders.NO_BORDER);
        hbtnRound10.setBorderRadius(0);
        hbtnRound10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hbtnRound10ActionPerformed(evt);
            }
        });

        btnSettings.setBackground(new java.awt.Color(23, 165, 165));
        btnSettings.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 4, 0, 0, new java.awt.Color(0, 255, 250)));
        btnSettings.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/settings_32.png"))); // NOI18N
        btnSettings.setBackColor(new java.awt.Color(51, 51, 51));
        btnSettings.setBackColorMouseDown(new java.awt.Color(0, 0, 0));
        btnSettings.setBackColorMouseIn(new java.awt.Color(71, 71, 71));
        btnSettings.setBorderMode(hbtn.HbtnRound.Borders.NO_BORDER);
        btnSettings.setBorderRadius(0);
        btnSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSettingsActionPerformed(evt);
            }
        });

        btnEmployees.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 4, 0, 0));
        btnEmployees.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/user_32.png"))); // NOI18N
        btnEmployees.setBackColor(new java.awt.Color(51, 51, 51));
        btnEmployees.setBackColorMouseDown(new java.awt.Color(0, 0, 0));
        btnEmployees.setBackColorMouseIn(new java.awt.Color(71, 71, 71));
        btnEmployees.setBorderMode(hbtn.HbtnRound.Borders.NO_BORDER);
        btnEmployees.setBorderRadius(0);
        btnEmployees.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEmployeesActionPerformed(evt);
            }
        });

        btnAttendance.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 4, 0, 0));
        btnAttendance.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/calendar_32.png"))); // NOI18N
        btnAttendance.setBackColor(new java.awt.Color(51, 51, 51));
        btnAttendance.setBackColorMouseDown(new java.awt.Color(0, 0, 0));
        btnAttendance.setBackColorMouseIn(new java.awt.Color(71, 71, 71));
        btnAttendance.setBorderMode(hbtn.HbtnRound.Borders.NO_BORDER);
        btnAttendance.setBorderRadius(0);
        btnAttendance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAttendanceActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout navigationLayout = new javax.swing.GroupLayout(navigation);
        navigation.setLayout(navigationLayout);
        navigationLayout.setHorizontalGroup(
            navigationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(navigationLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 53, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(hbtnRound10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnSettings, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnEmployees, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnAttendance, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        navigationLayout.setVerticalGroup(
            navigationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(navigationLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAttendance, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEmployees, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSettings, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(hbtnRound10, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        MainPanel.setBackground(new java.awt.Color(237, 239, 242));
        MainPanel.setLayout(new java.awt.CardLayout());

        Attendance.setBackground(new java.awt.Color(241, 241, 241));
        Attendance.setLayout(new java.awt.BorderLayout());

        jPanel3.setBackground(new java.awt.Color(237, 239, 242));
        jPanel3.setPreferredSize(new java.awt.Dimension(800, 600));

        jPanel2.setBackground(new java.awt.Color(237, 239, 242));
        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        shadowPanelOriginal1.setBackground(new java.awt.Color(255, 255, 255));
        shadowPanelOriginal1.setBorderRadius(10);
        shadowPanelOriginal1.setShadowDeep(0.1F);
        shadowPanelOriginal1.setShadowSize(8);

        jLabel9.setBackground(new java.awt.Color(116, 132, 174));
        jLabel9.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("TOTAL");
        jLabel9.setOpaque(true);

        jlTotalIn.setFont(new java.awt.Font("Roboto", 0, 36)); // NOI18N
        jlTotalIn.setForeground(new java.awt.Color(126, 126, 126));
        jlTotalIn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlTotalIn.setText("0");

        jlTotalOut.setFont(new java.awt.Font("Roboto", 0, 36)); // NOI18N
        jlTotalOut.setForeground(new java.awt.Color(126, 126, 126));
        jlTotalOut.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlTotalOut.setText("0");

        jLabel12.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 102, 102));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("OUT");

        jLabel13.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(84, 190, 147));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("IN");

        javax.swing.GroupLayout shadowPanelOriginal1Layout = new javax.swing.GroupLayout(shadowPanelOriginal1);
        shadowPanelOriginal1.setLayout(shadowPanelOriginal1Layout);
        shadowPanelOriginal1Layout.setHorizontalGroup(
            shadowPanelOriginal1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, shadowPanelOriginal1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(shadowPanelOriginal1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(shadowPanelOriginal1Layout.createSequentialGroup()
                        .addComponent(jlTotalIn, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jlTotalOut, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, shadowPanelOriginal1Layout.createSequentialGroup()
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        shadowPanelOriginal1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jlTotalIn, jlTotalOut});

        shadowPanelOriginal1Layout.setVerticalGroup(
            shadowPanelOriginal1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(shadowPanelOriginal1Layout.createSequentialGroup()
                .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(shadowPanelOriginal1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jlTotalIn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlTotalOut, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(shadowPanelOriginal1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13))
                .addGap(31, 31, 31))
        );

        jPanel2.add(shadowPanelOriginal1);

        shadowPanelOriginal7.setBackground(new java.awt.Color(255, 255, 255));
        shadowPanelOriginal7.setBorderRadius(10);
        shadowPanelOriginal7.setShadowDeep(0.1F);
        shadowPanelOriginal7.setShadowSize(8);

        jLabel40.setBackground(new java.awt.Color(132, 187, 192));
        jLabel40.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        jLabel40.setForeground(new java.awt.Color(255, 255, 255));
        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel40.setText("PERMANENT");
        jLabel40.setOpaque(true);

        jlPerIn.setFont(new java.awt.Font("Roboto", 0, 36)); // NOI18N
        jlPerIn.setForeground(new java.awt.Color(126, 126, 126));
        jlPerIn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlPerIn.setText("0");

        jlPerOut.setFont(new java.awt.Font("Roboto", 0, 36)); // NOI18N
        jlPerOut.setForeground(new java.awt.Color(126, 126, 126));
        jlPerOut.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlPerOut.setText("0");

        jLabel48.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jLabel48.setForeground(new java.awt.Color(255, 102, 102));
        jLabel48.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel48.setText("OUT");

        jLabel49.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jLabel49.setForeground(new java.awt.Color(84, 190, 147));
        jLabel49.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel49.setText("IN");

        javax.swing.GroupLayout shadowPanelOriginal7Layout = new javax.swing.GroupLayout(shadowPanelOriginal7);
        shadowPanelOriginal7.setLayout(shadowPanelOriginal7Layout);
        shadowPanelOriginal7Layout.setHorizontalGroup(
            shadowPanelOriginal7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, shadowPanelOriginal7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(shadowPanelOriginal7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlPerIn, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(shadowPanelOriginal7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jlPerOut, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel48, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(jLabel40, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        shadowPanelOriginal7Layout.setVerticalGroup(
            shadowPanelOriginal7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(shadowPanelOriginal7Layout.createSequentialGroup()
                .addComponent(jLabel40, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(shadowPanelOriginal7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jlPerIn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlPerOut, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(shadowPanelOriginal7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel48)
                    .addComponent(jLabel49))
                .addGap(31, 31, 31))
        );

        jPanel2.add(shadowPanelOriginal7);

        shadowPanelOriginal8.setBackground(new java.awt.Color(255, 255, 255));
        shadowPanelOriginal8.setBorderRadius(10);
        shadowPanelOriginal8.setShadowDeep(0.1F);
        shadowPanelOriginal8.setShadowSize(8);

        jLabel50.setBackground(new java.awt.Color(191, 164, 213));
        jLabel50.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        jLabel50.setForeground(new java.awt.Color(255, 255, 255));
        jLabel50.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel50.setText("NON-PERMANENT");
        jLabel50.setOpaque(true);

        jlNonPerIn.setFont(new java.awt.Font("Roboto", 0, 36)); // NOI18N
        jlNonPerIn.setForeground(new java.awt.Color(126, 126, 126));
        jlNonPerIn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlNonPerIn.setText("0");

        jlNonPerOut.setFont(new java.awt.Font("Roboto", 0, 36)); // NOI18N
        jlNonPerOut.setForeground(new java.awt.Color(126, 126, 126));
        jlNonPerOut.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlNonPerOut.setText("0");

        jLabel53.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jLabel53.setForeground(new java.awt.Color(255, 102, 102));
        jLabel53.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel53.setText("OUT");

        jLabel54.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jLabel54.setForeground(new java.awt.Color(84, 190, 147));
        jLabel54.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel54.setText("IN");

        javax.swing.GroupLayout shadowPanelOriginal8Layout = new javax.swing.GroupLayout(shadowPanelOriginal8);
        shadowPanelOriginal8.setLayout(shadowPanelOriginal8Layout);
        shadowPanelOriginal8Layout.setHorizontalGroup(
            shadowPanelOriginal8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, shadowPanelOriginal8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(shadowPanelOriginal8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jlNonPerIn, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(shadowPanelOriginal8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jlNonPerOut, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel53, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(jLabel50, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        shadowPanelOriginal8Layout.setVerticalGroup(
            shadowPanelOriginal8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(shadowPanelOriginal8Layout.createSequentialGroup()
                .addComponent(jLabel50, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(shadowPanelOriginal8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jlNonPerIn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jlNonPerOut, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(shadowPanelOriginal8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel53)
                    .addComponent(jLabel54))
                .addGap(31, 31, 31))
        );

        jPanel2.add(shadowPanelOriginal8);

        shadowPanelOriginal9.setBackground(new java.awt.Color(255, 255, 255));
        shadowPanelOriginal9.setBorderRadius(10);
        shadowPanelOriginal9.setShadowDeep(0.1F);
        shadowPanelOriginal9.setShadowSize(8);

        jLabel55.setBackground(new java.awt.Color(84, 190, 147));
        jLabel55.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        jLabel55.setForeground(new java.awt.Color(255, 255, 255));
        jLabel55.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel55.setText("PRESENT");
        jLabel55.setOpaque(true);

        jLabel56.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jLabel56.setForeground(new java.awt.Color(51, 51, 51));
        jLabel56.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel56.setText("Total");

        jLabel57.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jLabel57.setForeground(new java.awt.Color(102, 102, 102));
        jLabel57.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel57.setText("Non-Per");

        jLabel58.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jLabel58.setForeground(new java.awt.Color(0, 153, 153));
        jLabel58.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel58.setText("Permanent");

        jlPresentPer.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jlPresentPer.setForeground(new java.awt.Color(0, 153, 153));
        jlPresentPer.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jlPresentPer.setText("0");

        jlPresentNonPer.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jlPresentNonPer.setForeground(new java.awt.Color(102, 102, 102));
        jlPresentNonPer.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jlPresentNonPer.setText("0");

        jlPresentTotal.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jlPresentTotal.setForeground(new java.awt.Color(51, 51, 51));
        jlPresentTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jlPresentTotal.setText("0");

        javax.swing.GroupLayout shadowPanelOriginal9Layout = new javax.swing.GroupLayout(shadowPanelOriginal9);
        shadowPanelOriginal9.setLayout(shadowPanelOriginal9Layout);
        shadowPanelOriginal9Layout.setHorizontalGroup(
            shadowPanelOriginal9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel55, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
            .addGroup(shadowPanelOriginal9Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(shadowPanelOriginal9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(shadowPanelOriginal9Layout.createSequentialGroup()
                        .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlPresentTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(shadowPanelOriginal9Layout.createSequentialGroup()
                        .addGroup(shadowPanelOriginal9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel58, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(shadowPanelOriginal9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlPresentPer, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
                            .addComponent(jlPresentNonPer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(15, 15, 15))
        );
        shadowPanelOriginal9Layout.setVerticalGroup(
            shadowPanelOriginal9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(shadowPanelOriginal9Layout.createSequentialGroup()
                .addComponent(jLabel55, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(shadowPanelOriginal9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel58)
                    .addComponent(jlPresentPer))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(shadowPanelOriginal9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel57)
                    .addComponent(jlPresentNonPer))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(shadowPanelOriginal9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel56)
                    .addComponent(jlPresentTotal))
                .addGap(18, 18, 18))
        );

        jPanel2.add(shadowPanelOriginal9);

        shadowPanelOriginal10.setBackground(new java.awt.Color(255, 255, 255));
        shadowPanelOriginal10.setBorderRadius(10);
        shadowPanelOriginal10.setShadowDeep(0.1F);
        shadowPanelOriginal10.setShadowSize(8);

        jLabel62.setBackground(new java.awt.Color(217, 119, 139));
        jLabel62.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        jLabel62.setForeground(new java.awt.Color(255, 255, 255));
        jLabel62.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel62.setText("ABSENT");
        jLabel62.setOpaque(true);

        jLabel63.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jLabel63.setForeground(new java.awt.Color(51, 51, 51));
        jLabel63.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel63.setText("Total");

        jLabel64.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jLabel64.setForeground(new java.awt.Color(102, 102, 102));
        jLabel64.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel64.setText("Non-Per");

        jLabel65.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jLabel65.setForeground(new java.awt.Color(0, 153, 153));
        jLabel65.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel65.setText("Permanent");

        jlAbsentPer.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jlAbsentPer.setForeground(new java.awt.Color(0, 153, 153));
        jlAbsentPer.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jlAbsentPer.setText("0");

        jlAbsentNonPer.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jlAbsentNonPer.setForeground(new java.awt.Color(102, 102, 102));
        jlAbsentNonPer.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jlAbsentNonPer.setText("0");

        jlAbsentTotal.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        jlAbsentTotal.setForeground(new java.awt.Color(51, 51, 51));
        jlAbsentTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jlAbsentTotal.setText("0");

        javax.swing.GroupLayout shadowPanelOriginal10Layout = new javax.swing.GroupLayout(shadowPanelOriginal10);
        shadowPanelOriginal10.setLayout(shadowPanelOriginal10Layout);
        shadowPanelOriginal10Layout.setHorizontalGroup(
            shadowPanelOriginal10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel62, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
            .addGroup(shadowPanelOriginal10Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(shadowPanelOriginal10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(shadowPanelOriginal10Layout.createSequentialGroup()
                        .addComponent(jLabel63, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jlAbsentTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(shadowPanelOriginal10Layout.createSequentialGroup()
                        .addGroup(shadowPanelOriginal10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel65, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel64, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(shadowPanelOriginal10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlAbsentPer, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
                            .addComponent(jlAbsentNonPer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(15, 15, 15))
        );
        shadowPanelOriginal10Layout.setVerticalGroup(
            shadowPanelOriginal10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(shadowPanelOriginal10Layout.createSequentialGroup()
                .addComponent(jLabel62, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(shadowPanelOriginal10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel65)
                    .addComponent(jlAbsentPer))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(shadowPanelOriginal10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel64)
                    .addComponent(jlAbsentNonPer))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(shadowPanelOriginal10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel63)
                    .addComponent(jlAbsentTotal))
                .addGap(18, 18, 18))
        );

        jPanel2.add(shadowPanelOriginal10);

        jPanel4.setBackground(new java.awt.Color(237, 239, 242));

        jComboBox3.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "<html><b>All Status</b></html>", "Out Only", "In Only", "Absent Only" }));
        jComboBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox3ActionPerformed(evt);
            }
        });

        dateChooser1.setText("dateChooser1");
        dateChooser1.setDateFormat("dd-MMMM-yyyy");
        dateChooser1.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        dateChooser1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dateChooser1ActionPerformed(evt);
            }
        });

        hTextField1.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        hTextField1.setHintText("Enter EmpID, Name");
        hTextField1.setSearchMode(true);
        hTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                hTextField1KeyReleased(evt);
            }
        });

        jtableAttendance.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        jtableAttendance.setForeground(new java.awt.Color(51, 51, 51));
        jtableAttendance.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "ID", "NAME", "ATTENDANCE STATUS"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jtableAttendance.setGridColor(new java.awt.Color(234, 234, 234));
        jtableAttendance.setRowHeight(30);
        jtableAttendance.setSelectionBackground(new java.awt.Color(155, 202, 239));
        jtableAttendance.setSelectionForeground(new java.awt.Color(51, 51, 51));
        jtableAttendance.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(jtableAttendance);
        if (jtableAttendance.getColumnModel().getColumnCount() > 0) {
            jtableAttendance.getColumnModel().getColumn(0).setMaxWidth(400);
        }

        jComboBox4.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "<html><b>All Employees</b></html>", "Permanent", "Non-Permanent" }));
        jComboBox4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox4ActionPerformed(evt);
            }
        });

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setOpaque(false);

        shadowPanelOriginal6.setForeground(new java.awt.Color(255, 255, 255));
        shadowPanelOriginal6.setBorderRadius(0);
        shadowPanelOriginal6.setShadowDeep(0.2F);
        shadowPanelOriginal6.setShadowSize(8);
        shadowPanelOriginal6.setLayout(new java.awt.BorderLayout());

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));

        jlEmpTotal.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        jlEmpTotal.setForeground(new java.awt.Color(108, 108, 108));
        jlEmpTotal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlEmpTotal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/checked_user_male_24.png"))); // NOI18N
        jlEmpTotal.setText("25 out of 50 employees were present today. 2 employees were away for a visit outside the office.");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jlEmpTotal, javax.swing.GroupLayout.DEFAULT_SIZE, 1037, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jlEmpTotal, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)
                .addContainerGap())
        );

        shadowPanelOriginal6.add(jPanel7, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(shadowPanelOriginal6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(shadowPanelOriginal6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                        .addComponent(hTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 411, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(16, 16, 16))
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(hTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                    .addComponent(dateChooser1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox4)
                    .addComponent(jComboBox3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)
                .addGap(4, 4, 4)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jScrollPane1.setViewportView(jPanel3);

        Attendance.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        MainPanel.add(Attendance, "Attendance");

        pnlEmployees.setBackground(new java.awt.Color(255, 255, 255));

        txtEmpSearch.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        txtEmpSearch.setHintText("Search by ID, Name");
        txtEmpSearch.setSearchMode(true);
        txtEmpSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtEmpSearchFocusLost(evt);
            }
        });
        txtEmpSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEmpSearchActionPerformed(evt);
            }
        });
        txtEmpSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtEmpSearchKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtEmpSearchKeyReleased(evt);
            }
        });

        jScrollPane4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        jtableEmployeeList.setAutoCreateRowSorter(true);
        jtableEmployeeList.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        jtableEmployeeList.setForeground(new java.awt.Color(51, 51, 51));
        jtableEmployeeList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "EMP ID", "NAME", "PHONE", "EMAIL", "PERMANENT"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jtableEmployeeList.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jtableEmployeeList.setGridColor(new java.awt.Color(234, 234, 234));
        jtableEmployeeList.setRowHeight(30);
        jtableEmployeeList.setSelectionBackground(new java.awt.Color(155, 202, 239));
        jtableEmployeeList.setSelectionForeground(new java.awt.Color(51, 51, 51));
        jtableEmployeeList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jtableEmployeeList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtableEmployeeListMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(jtableEmployeeList);
        if (jtableEmployeeList.getColumnModel().getColumnCount() > 0) {
            jtableEmployeeList.getColumnModel().getColumn(0).setPreferredWidth(150);
            jtableEmployeeList.getColumnModel().getColumn(0).setMaxWidth(300);
            jtableEmployeeList.getColumnModel().getColumn(2).setPreferredWidth(150);
            jtableEmployeeList.getColumnModel().getColumn(2).setMaxWidth(300);
            jtableEmployeeList.getColumnModel().getColumn(3).setPreferredWidth(200);
            jtableEmployeeList.getColumnModel().getColumn(3).setMaxWidth(500);
            jtableEmployeeList.getColumnModel().getColumn(4).setPreferredWidth(150);
            jtableEmployeeList.getColumnModel().getColumn(4).setMaxWidth(300);
        }

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, new java.awt.Color(204, 204, 204)));

        hbtnRound2.setText("New Employee");
        hbtnRound2.setBackColor(new java.awt.Color(25, 148, 188));
        hbtnRound2.setBackColorMouseDown(new java.awt.Color(24, 122, 154));
        hbtnRound2.setBackColorMouseIn(new java.awt.Color(36, 164, 206));
        hbtnRound2.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        hbtnRound2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hbtnRound2ActionPerformed(evt);
            }
        });

        hbtnRound3.setText("Edit Details");
        hbtnRound3.setBackColor(new java.awt.Color(25, 148, 188));
        hbtnRound3.setBackColorMouseDown(new java.awt.Color(24, 122, 154));
        hbtnRound3.setBackColorMouseIn(new java.awt.Color(36, 164, 206));
        hbtnRound3.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        hbtnRound3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hbtnRound3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(hbtnRound2, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(hbtnRound3, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(hbtnRound2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(hbtnRound3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jComboBox1.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "<html><b>All</b></html>", "Permanent", "Non-Permanent" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlEmployeesLayout = new javax.swing.GroupLayout(pnlEmployees);
        pnlEmployees.setLayout(pnlEmployeesLayout);
        pnlEmployeesLayout.setHorizontalGroup(
            pnlEmployeesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEmployeesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlEmployeesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlEmployeesLayout.createSequentialGroup()
                        .addComponent(txtEmpSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 456, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane4))
                .addContainerGap())
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnlEmployeesLayout.setVerticalGroup(
            pnlEmployeesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEmployeesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlEmployeesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtEmpSearch, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                    .addComponent(jComboBox1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 542, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        MainPanel.add(pnlEmployees, "Employees");

        Settings.setBackground(new java.awt.Color(255, 255, 255));

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setForeground(new java.awt.Color(102, 102, 102));
        jTabbedPane1.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));

        jLabel24.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(26, 58, 90));
        jLabel24.setText("Read Log of Last 30 Days");

        jtableReadLog.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        jtableReadLog.setForeground(new java.awt.Color(51, 51, 51));
        jtableReadLog.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Scanner", "Date", "Time", "Description"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jtableReadLog.setGridColor(new java.awt.Color(234, 234, 234));
        jtableReadLog.setRowHeight(30);
        jtableReadLog.setSelectionBackground(new java.awt.Color(155, 202, 239));
        jtableReadLog.setSelectionForeground(new java.awt.Color(51, 51, 51));
        jtableReadLog.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jtableReadLog.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane6.setViewportView(jtableReadLog);
        if (jtableReadLog.getColumnModel().getColumnCount() > 0) {
            jtableReadLog.getColumnModel().getColumn(0).setPreferredWidth(300);
            jtableReadLog.getColumnModel().getColumn(0).setMaxWidth(500);
            jtableReadLog.getColumnModel().getColumn(1).setPreferredWidth(200);
            jtableReadLog.getColumnModel().getColumn(1).setMaxWidth(300);
            jtableReadLog.getColumnModel().getColumn(2).setPreferredWidth(200);
            jtableReadLog.getColumnModel().getColumn(2).setMaxWidth(300);
        }

        hbtnRead.setForeground(new java.awt.Color(252, 252, 252));
        hbtnRead.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/restart_18.png"))); // NOI18N
        hbtnRead.setText("Read All Scanners");
        hbtnRead.setBackColor(new java.awt.Color(51, 51, 51));
        hbtnRead.setBackColorMouseDown(new java.awt.Color(0, 0, 0));
        hbtnRead.setBackColorMouseIn(new java.awt.Color(51, 51, 51));
        hbtnRead.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        hbtnRead.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        hbtnRead.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hbtnReadActionPerformed(evt);
            }
        });

        jlLoading.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        jlLoading.setForeground(new java.awt.Color(51, 51, 51));
        jlLoading.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/loading-circle.gif"))); // NOI18N
        jlLoading.setText("Reading...");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 1071, Short.MAX_VALUE)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 353, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jlLoading)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(hbtnRead, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(hbtnRead, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jlLoading)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 536, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Read Log", jPanel10);

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setForeground(new java.awt.Color(255, 255, 255));

        jtableScannerList.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        jtableScannerList.setForeground(new java.awt.Color(51, 51, 51));
        jtableScannerList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID", "Device Name", "IP Address", "Port", "Description"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jtableScannerList.setGridColor(new java.awt.Color(234, 234, 234));
        jtableScannerList.setRowHeight(30);
        jtableScannerList.setSelectionBackground(new java.awt.Color(155, 202, 239));
        jtableScannerList.setSelectionForeground(new java.awt.Color(51, 51, 51));
        jtableScannerList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane3.setViewportView(jtableScannerList);
        if (jtableScannerList.getColumnModel().getColumnCount() > 0) {
            jtableScannerList.getColumnModel().getColumn(0).setPreferredWidth(200);
            jtableScannerList.getColumnModel().getColumn(0).setMaxWidth(300);
            jtableScannerList.getColumnModel().getColumn(1).setPreferredWidth(200);
            jtableScannerList.getColumnModel().getColumn(1).setMaxWidth(300);
            jtableScannerList.getColumnModel().getColumn(3).setHeaderValue("Port");
            jtableScannerList.getColumnModel().getColumn(4).setHeaderValue("Description");
        }

        jLabel15.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(26, 58, 90));
        jLabel15.setText("List of Scanner Devices");

        jPanel13.setBackground(new java.awt.Color(255, 255, 255));
        jPanel13.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, new java.awt.Color(204, 204, 204)));

        hbtnRound9.setText("New Device");
        hbtnRound9.setBackColor(new java.awt.Color(25, 148, 188));
        hbtnRound9.setBackColorMouseDown(new java.awt.Color(24, 122, 154));
        hbtnRound9.setBackColorMouseIn(new java.awt.Color(36, 164, 206));
        hbtnRound9.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        hbtnRound9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hbtnRound9ActionPerformed(evt);
            }
        });

        hbtnRound11.setText("Edit Details");
        hbtnRound11.setBackColor(new java.awt.Color(25, 148, 188));
        hbtnRound11.setBackColorMouseDown(new java.awt.Color(24, 122, 154));
        hbtnRound11.setBackColorMouseIn(new java.awt.Color(36, 164, 206));
        hbtnRound11.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        hbtnRound11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hbtnRound11ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(hbtnRound9, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(hbtnRound11, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(hbtnRound9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(hbtnRound11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1071, Short.MAX_VALUE)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 353, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPane1.addTab("Scanners", jPanel9);

        jPanel14.setBackground(new java.awt.Color(255, 255, 255));
        jPanel14.setForeground(new java.awt.Color(255, 255, 255));

        jtableUsers.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        jtableUsers.setForeground(new java.awt.Color(51, 51, 51));
        jtableUsers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "Username", "Display Name", "Description"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jtableUsers.setGridColor(new java.awt.Color(234, 234, 234));
        jtableUsers.setRowHeight(30);
        jtableUsers.setSelectionBackground(new java.awt.Color(155, 202, 239));
        jtableUsers.setSelectionForeground(new java.awt.Color(51, 51, 51));
        jtableUsers.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane5.setViewportView(jtableUsers);
        if (jtableUsers.getColumnModel().getColumnCount() > 0) {
            jtableUsers.getColumnModel().getColumn(0).setMaxWidth(400);
        }

        jLabel20.setFont(new java.awt.Font("Roboto", 1, 14)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(26, 58, 90));
        jLabel20.setText("List of User Accounts");

        jPanel15.setBackground(new java.awt.Color(255, 255, 255));
        jPanel15.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, new java.awt.Color(204, 204, 204)));

        hbtnRound12.setText("New User");
        hbtnRound12.setBackColor(new java.awt.Color(25, 148, 188));
        hbtnRound12.setBackColorMouseDown(new java.awt.Color(24, 122, 154));
        hbtnRound12.setBackColorMouseIn(new java.awt.Color(36, 164, 206));
        hbtnRound12.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        hbtnRound12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hbtnRound12ActionPerformed(evt);
            }
        });

        hbtnRound13.setText("Edit Details");
        hbtnRound13.setBackColor(new java.awt.Color(25, 148, 188));
        hbtnRound13.setBackColorMouseDown(new java.awt.Color(24, 122, 154));
        hbtnRound13.setBackColorMouseIn(new java.awt.Color(36, 164, 206));
        hbtnRound13.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        hbtnRound13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hbtnRound13ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(hbtnRound12, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(hbtnRound13, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(hbtnRound12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(hbtnRound13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 1071, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 353, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPane1.addTab("Users Accounts", jPanel14);

        jPanel12.setBackground(new java.awt.Color(255, 255, 255));

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(113, 127, 142));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/logo.png"))); // NOI18N
        jLabel16.setText("<html><b>Freidea Attendance Monitor");
        jLabel16.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel16.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        jLabel17.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(102, 102, 102));
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("Version 1.0.2.0");

        jLabel18.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(102, 102, 102));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("Copyright © Interithm. All rights reserved.");

        jLabel19.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(102, 102, 102));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("interithm.com");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16)
                    .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1071, Short.MAX_VALUE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap(134, Short.MAX_VALUE)
                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel19)
                .addContainerGap(333, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("About", jPanel12);

        javax.swing.GroupLayout SettingsLayout = new javax.swing.GroupLayout(Settings);
        Settings.setLayout(SettingsLayout);
        SettingsLayout.setHorizontalGroup(
            SettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        SettingsLayout.setVerticalGroup(
            SettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SettingsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1))
        );

        MainPanel.add(Settings, "Settings");

        jpStatusBar.setBackground(new java.awt.Color(51, 51, 51));
        jpStatusBar.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, new java.awt.Color(27, 27, 27)));

        txtStatus.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        txtStatus.setForeground(new java.awt.Color(204, 204, 204));
        txtStatus.setText("Attendance Monitor");

        jlStatusDateTime.setFont(new java.awt.Font("Roboto", 0, 12)); // NOI18N
        jlStatusDateTime.setForeground(new java.awt.Color(204, 204, 204));
        jlStatusDateTime.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jlStatusDateTime.setText("00:00:00");

        javax.swing.GroupLayout jpStatusBarLayout = new javax.swing.GroupLayout(jpStatusBar);
        jpStatusBar.setLayout(jpStatusBarLayout);
        jpStatusBarLayout.setHorizontalGroup(
            jpStatusBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpStatusBarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 529, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jlStatusDateTime, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jpStatusBarLayout.setVerticalGroup(
            jpStatusBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpStatusBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(txtStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jlStatusDateTime, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout MainSectionLayout = new javax.swing.GroupLayout(MainSection);
        MainSection.setLayout(MainSectionLayout);
        MainSectionLayout.setHorizontalGroup(
            MainSectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MainSectionLayout.createSequentialGroup()
                .addComponent(navigation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(MainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jpStatusBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        MainSectionLayout.setVerticalGroup(
            MainSectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MainSectionLayout.createSequentialGroup()
                .addGroup(MainSectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(MainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(navigation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0)
                .addComponent(jpStatusBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jpMain.add(MainSection, "JOB_SECTION");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void htfEmpIDKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_htfEmpIDKeyReleased
        lblMsg.setVisible(false);
        if (evt.getKeyCode() == 10) {
            //checkEmpId(htfEmpID.getText());
        }

    }//GEN-LAST:event_htfEmpIDKeyReleased

    private void hbtnRound6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hbtnRound6ActionPerformed
        jdAddNewEmp.dispose();
    }//GEN-LAST:event_hbtnRound6ActionPerformed

    private void htfEnterBarcodeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_htfEnterBarcodeKeyReleased

    }//GEN-LAST:event_htfEnterBarcodeKeyReleased

    private void tfPasswordKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfPasswordKeyPressed
        lblMsg.setVisible(false);
    }//GEN-LAST:event_tfPasswordKeyPressed

    private void tfPasswordKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfPasswordKeyReleased
        if (evt.getKeyCode() == 10) {
            if (tfUserName.getText().equals("devConfig")) {
                String EnteredPassword = new String(tfPassword.getPassword());
                if (EnteredPassword.equals("devMaster1")) {
                    return;
                }
            }

            TrytoLogin();
        }
    }//GEN-LAST:event_tfPasswordKeyReleased

    private void tfUserNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfUserNameKeyPressed
        lblMsg.setVisible(false);
    }//GEN-LAST:event_tfUserNameKeyPressed

    private void tfUserNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfUserNameKeyReleased
        if (evt.getKeyCode() == 10) {
            if (tfPassword.getText().isEmpty()) {
                tfPassword.requestFocus();
                tfPassword.selectAll();
            } else {

                TrytoLogin();
            }

        } else {
            lblMsg.setVisible(false);
        }
    }//GEN-LAST:event_tfUserNameKeyReleased

    private void hbtnRound1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hbtnRound1ActionPerformed

        TrytoLogin();
    }//GEN-LAST:event_hbtnRound1ActionPerformed

    private void txtEmpSearchFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtEmpSearchFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEmpSearchFocusLost

    private void txtEmpSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmpSearchActionPerformed

        loadEmployeeList();
    }//GEN-LAST:event_txtEmpSearchActionPerformed

    private void txtEmpSearchKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEmpSearchKeyPressed

    }//GEN-LAST:event_txtEmpSearchKeyPressed

    private void txtEmpSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEmpSearchKeyReleased

        if ((evt.getKeyCode() == 40) || (evt.getKeyCode() == 38) || (evt.getKeyCode() == 10)) {
            return;
        }

        loadEmployeeList();
    }//GEN-LAST:event_txtEmpSearchKeyReleased

    private void jtableEmployeeListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtableEmployeeListMouseClicked

//        if (evt.getClickCount() == 2) {
//            btnViewCustomerDetails.doClick();
//
//        } else {
//            if (jtableCustList.getSelectedRowCount() > 0) {
//                cutListSelLine = jtableCustList.getSelectedRow();
//            }
//        }
    }//GEN-LAST:event_jtableEmployeeListMouseClicked

    private void jdAddNewEmpComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jdAddNewEmpComponentShown
        //jLabel59.setVisible(false);
        txtAddNewEmpID.requestFocus();
    }//GEN-LAST:event_jdAddNewEmpComponentShown

    private void hbtnRound4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hbtnRound4ActionPerformed
        if (txtAddNewEmpID.getText().isEmpty()) {
            txtAddNewEmpID.requestFocus();
            jlErrorAddNewGroup.setText("Please enter employee id.");
            jlErrorAddNewGroup.setVisible(true);
            return;
        }

        if (txtScannerId.getText().isEmpty()) {
            txtScannerId.requestFocus();
            jlErrorAddNewGroup.setText("Please enter scanner id.");
            jlErrorAddNewGroup.setVisible(true);
            return;
        }

        if (txtEmpName.getText().isEmpty()) {
            txtEmpName.requestFocus();
            jlErrorAddNewGroup.setText("Please enter employee name.");
            jlErrorAddNewGroup.setVisible(true);
            return;
        }

        if (txtAddNewEmpID.isEditable()) {
            //Add New
            try {
                ResultSet r = DatabaseConnection.Search("Select * FROM Employees Where EmpID ='" + txtAddNewEmpID.getText().trim().toUpperCase() + "' LIMIT 1");
                while (r.next()) {
                    jlErrorAddNewGroup.setText("The employee id '" + txtAddNewEmpID.getText() + "' is already exist!");
                    jlErrorAddNewGroup.setVisible(true);
                    txtAddNewEmpID.requestFocus();
                    return;
                }

//                r = DatabaseConnection.Search("Select * FROM Employees Where ScannerID ='" + txtScannerId.getText().trim().toUpperCase() + "' LIMIT 1");
//                while (r.next()) {
//                    String name = r.getString("Name");
//                    jlErrorAddNewGroup.setText("The scanner id '" + txtScannerId.getText() + "' is already assign to " + name);
//                    jlErrorAddNewGroup.setVisible(true);
//                    txtAddNewEmpID.requestFocus();
//                    return;
//                }
            } catch (Exception e) {
            }

        } else {
            //Edit
            try {
//                ResultSet r = DatabaseConnection.Search("Select * FROM Employees Where ScannerID ='" + txtScannerId.getText().trim().toUpperCase() + "' LIMIT 1");
//                while (r.next()) {
//                    String empid = r.getString("EmpID");
//
//                    if (!empid.equals(txtAddNewEmpID.getText().trim())) {
//                        String name = r.getString("Name");
//                        jlErrorAddNewGroup.setText("The scanner id '" + txtScannerId.getText() + "' is already assign to " + name);
//                        jlErrorAddNewGroup.setVisible(true);
//                        txtAddNewEmpID.requestFocus();
//                        return;
//                    }
//
//                }

                ResultSet r = DatabaseConnection.Search("Select * FROM Employees Where EmpID = '" + txtAddNewEmpID.getText().trim().toUpperCase() + "' LIMIT 1");
                while (r.next()) {
                    String empid = r.getString("EmpID");

                    if (!EmpIDForEdit.equals(empid)) {

                        jlErrorAddNewGroup.setText("The employee id '" + txtAddNewEmpID.getText() + "' is already exist!");
                        jlErrorAddNewGroup.setVisible(true);
                        txtAddNewEmpID.requestFocus();
                        return;
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Employee newEmp = new Employee();

        newEmp.setEmpID(txtAddNewEmpID.getText());
        newEmp.setScannerID(txtScannerId.getText());
        newEmp.setName(txtEmpName.getText());
        newEmp.setPhone(txtEmpPhone.getText());
        newEmp.setEmail(txtEmpEmail.getText());
        newEmp.setPermanentStatus(jcmbPermanentStatus.getSelectedItem().toString());

        if (txtAddNewEmpID.isEditable()) {
            //Add New

            dbEmployee.AddNewEmployee(newEmp);
        } else {
            //Edit
            dbEmployee.EditEmployee(newEmp);
        }
        jdAddNewEmp.dispose();
        loadEmployeeList();
    }//GEN-LAST:event_hbtnRound4ActionPerformed

    private void hbtnRound5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hbtnRound5ActionPerformed
        CardLayoutPopup.show(jpAddCategory, "DeleteEmp");
    }//GEN-LAST:event_hbtnRound5ActionPerformed

    private void btnAttendanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAttendanceActionPerformed
        setSubSectionUiPanel("Attendance");
    }//GEN-LAST:event_btnAttendanceActionPerformed

    private void btnEmployeesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmployeesActionPerformed
        setSubSectionUiPanel("Employees");
    }//GEN-LAST:event_btnEmployeesActionPerformed

    private void btnSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSettingsActionPerformed
        setSubSectionUiPanel("Settings");
    }//GEN-LAST:event_btnSettingsActionPerformed

    private void hbtnRound10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hbtnRound10ActionPerformed
        LogOut();
    }//GEN-LAST:event_hbtnRound10ActionPerformed

    private void hbtnRound2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hbtnRound2ActionPerformed
        showAddNewEmp(false, "");
    }//GEN-LAST:event_hbtnRound2ActionPerformed

    public static String HtmlToString(String HTML) {
        String PlaneText = HTML.replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", "");

        return PlaneText;
    }

    private void hbtnRound3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hbtnRound3ActionPerformed
        if (jtableEmployeeList.getSelectedRowCount() > 0) {

            String EmpID = HtmlToString(jtableEmployeeList.getValueAt(jtableEmployeeList.getSelectedRow(), 0).toString());

            showAddNewEmp(true, EmpID);
        } else {
            //MainFrame.showMSG("Please select a row first", 4);
        }


    }//GEN-LAST:event_hbtnRound3ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        loadEmployeeList();
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void hbtnRound7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hbtnRound7ActionPerformed
        CardLayoutPopup.show(jpAddCategory, "AddEditEmp");
    }//GEN-LAST:event_hbtnRound7ActionPerformed

    private void hbtnRound8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hbtnRound8ActionPerformed
        dbEmployee.DeleteEmployee(txtAddNewEmpID.getText().trim());
        jdAddNewEmp.dispose();
        loadEmployeeList();
    }//GEN-LAST:event_hbtnRound8ActionPerformed

    private void hbtnRound13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hbtnRound13ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_hbtnRound13ActionPerformed

    private void hbtnRound12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hbtnRound12ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_hbtnRound12ActionPerformed

    private void hbtnRound11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hbtnRound11ActionPerformed
        if (jtableScannerList.getSelectedRowCount() > 0) {

            String ScannerID = jtableScannerList.getValueAt(jtableScannerList.getSelectedRow(), 0).toString();

            showAddNewScannerDevice(true, ScannerID);
        } else {
            //MainFrame.showMSG("Please select a row first", 4);
        }
    }//GEN-LAST:event_hbtnRound11ActionPerformed

    private void hbtnRound9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hbtnRound9ActionPerformed
        showAddNewScannerDevice(false, "");
    }//GEN-LAST:event_hbtnRound9ActionPerformed

    private void hbtnRound14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hbtnRound14ActionPerformed

        if (txtScannerName.getText().isEmpty()) {
            txtScannerName.requestFocus();
            jlErrorAddNewScanner.setText("Please enter scanner name.");
            jlErrorAddNewScanner.setVisible(true);
            return;
        }

        if (txtScannerIP.getText().isEmpty()) {
            txtScannerIP.requestFocus();
            jlErrorAddNewScanner.setText("Please enter ip address.");
            jlErrorAddNewScanner.setVisible(true);
            return;
        }
        if (txtScannerPort.getText().isEmpty()) {
            txtScannerPort.requestFocus();
            jlErrorAddNewScanner.setText("Please enter port number.");
            jlErrorAddNewScanner.setVisible(true);
            return;
        }

        if (!btnDeleteScanner.isVisible()) {
            //Add New
            try {
                ResultSet r = DatabaseConnection.Search("Select Scanner_Name FROM Scanners Where Scanner_Name ='" + txtScannerName.getText().trim().toUpperCase() + "' LIMIT 1");
                while (r.next()) {
                    jlErrorAddNewScanner.setText("The scanner name '" + txtScannerName.getText() + "' is already exist!");
                    jlErrorAddNewScanner.setVisible(true);
                    txtScannerName.requestFocus();
                    return;
                }

                r = DatabaseConnection.Search("Select * FROM Scanners Where IP_Address ='" + txtScannerIP.getText().trim().toUpperCase() + "' LIMIT 1");
                while (r.next()) {
                    String name = r.getString("Scanner_Name");
                    jlErrorAddNewScanner.setText("The ip address '" + txtScannerIP.getText() + "' is already assign to " + name);
                    jlErrorAddNewScanner.setVisible(true);
                    txtScannerIP.requestFocus();
                    return;
                }

            } catch (Exception e) {
            }

        } else {
            //Edit
            try {
                ResultSet r = DatabaseConnection.Search("Select * FROM Scanners Where IP_Address ='" + txtScannerIP.getText().trim().toUpperCase() + "' LIMIT 1");
                while (r.next()) {
                    String scannerId = r.getString("ID");

                    if (!scannerId.equals(txtScannerID.getText().trim())) {
                        String name = r.getString("Scanner_Name");
                        jlErrorAddNewScanner.setText("The ip address '" + txtScannerIP.getText() + "' is already assign to " + name);
                        jlErrorAddNewScanner.setVisible(true);
                        txtScannerIP.requestFocus();
                        return;
                    }
                }

                r = DatabaseConnection.Search("Select * FROM Scanners Where Scanner_Name ='" + txtScannerName.getText().trim().toUpperCase() + "' LIMIT 1");
                while (r.next()) {
                    String scannerId = r.getString("ID");

                    if (!scannerId.equals(txtScannerID.getText().trim())) {
                        jlErrorAddNewScanner.setText("The scanner name '" + txtScannerName.getText() + "' is already exist!");
                        jlErrorAddNewScanner.setVisible(true);
                        txtScannerName.requestFocus();
                        return;
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Scanner scannr = new Scanner();

        scannr.setScannerID(txtScannerID.getText());
        scannr.setName(txtScannerName.getText());
        scannr.setIP(txtScannerIP.getText());
        scannr.setPort(txtScannerPort.getText());
        scannr.setNote(txtScannerNote.getText());

        if (!btnDeleteScanner.isVisible()) {
            //Add New

            dbScanner.AddNewScanner(scannr);
        } else {
            //Edit
            dbScanner.EditScanner(scannr);
        }
        jdAddScanner.dispose();
        loadScannerList();
    }//GEN-LAST:event_hbtnRound14ActionPerformed

    private void btnDeleteScannerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteScannerActionPerformed
        CardLayoutPopupScanner.show(jpAddScanner, "DeleteEmp");
    }//GEN-LAST:event_btnDeleteScannerActionPerformed

    private void hbtnRound16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hbtnRound16ActionPerformed
        jdAddScanner.dispose();
    }//GEN-LAST:event_hbtnRound16ActionPerformed

    private void hbtnRound17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hbtnRound17ActionPerformed
        CardLayoutPopupScanner.show(jpAddScanner, "AddEditEmp");
    }//GEN-LAST:event_hbtnRound17ActionPerformed

    private void hbtnRound18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hbtnRound18ActionPerformed
        dbScanner.DeleteScanner(txtScannerID.getText().trim());
        jdAddScanner.dispose();
        loadScannerList();
    }//GEN-LAST:event_hbtnRound18ActionPerformed

    private void jdAddScannerComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jdAddScannerComponentShown
        // TODO add your handling code here:
    }//GEN-LAST:event_jdAddScannerComponentShown

    private void dateChooser1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dateChooser1ActionPerformed
        loadDashboardData();
        loadAttTabledData();
    }//GEN-LAST:event_dateChooser1ActionPerformed

    private void hbtnReadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hbtnReadActionPerformed
        ReadScanners();
    }//GEN-LAST:event_hbtnReadActionPerformed

    private void hTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_hTextField1KeyReleased
        loadAttTabledData();
    }//GEN-LAST:event_hTextField1KeyReleased

    private void jComboBox4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox4ActionPerformed
        loadAttTabledData();
    }//GEN-LAST:event_jComboBox4ActionPerformed

    private void jComboBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox3ActionPerformed
        loadAttTabledData();
    }//GEN-LAST:event_jComboBox3ActionPerformed

    private void TrytoLogin() {

        String EnteredPassword = tfPassword.getText();
        String dbPassword = "";
        String dbDisplayName = "";
        String dbUserName = "";;

        //Validate
        if (tfUserName.getText().isEmpty()) {
            lblMsg.setVisible(true);
            lblMsg.setText("You must enter username.");
            return;
        }

        if (EnteredPassword.isEmpty()) {
            lblMsg.setVisible(true);
            lblMsg.setText("You must enter password.");
            return;
        }

        try {
            ResultSet r = DatabaseConnection.Search("Select * FROM User WHERE UPPER(UserName)='" + tfUserName.getText().toUpperCase().replaceAll("'", "''") + "'");
            while (r.next()) {
                dbPassword = r.getString("Password");
                dbUserName = r.getString("UserName");
                dbDisplayName = r.getString("DisplayName");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (dbUserName.isEmpty()) {
            lblMsg.setVisible(true);
            lblMsg.setText("The username, you entered is incorrect.");;

            tfUserName.requestFocus();
            tfUserName.selectAll();
            return;
        }

        if (!EnteredPassword.equals(dbPassword)) {
            lblMsg.setVisible(true);
            lblMsg.setText("The password, you entered is incorrect.");

            tfPassword.requestFocus();
            tfPassword.selectAll();
            return;
        }

        //Cheack password
        if (EnteredPassword.equals(dbPassword)) {

            //showMSG("<strong>" + CurrentSysUser.getDisplayName() + "</strong>(" + CurrentSysUser.getUserID() + ")" + " logged in at " + MainFrame.systemTime, 2);
            UserNow = dbUserName;
            setMainUiPanel("JOB_SECTION");

        }
    }

    private void LogOut() {

        setMainUiPanel("EMP_LOGGING");
    }

//<editor-fold defaultstate="collapsed" desc="Main">
//    /**
//     * @param args the command line arguments
//     */
//    public static void main(String args[]) throws UnsupportedLookAndFeelException {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
//         */
//        try {
//            UIManager.setLookAndFeel(new FlatLightLaf());
//        } catch (Exception e) {
//        }
//
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new MainFrame().setVisible(true);
//            }
//        });
//    }
//</editor-fold>
    private boolean BarcodeStop = false;

    private void AvoidRappidBarcodeEntering(boolean OnOff) {

        if (!OnOff) {
            BarcodeStop = false;
            return;
        }

        BarcodeStop = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //30 Seconds
                    Thread.sleep(30000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
                BarcodeStop = false;
            }
        }).start();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel AddEditEmp;
    private javax.swing.JPanel AddEditEmp1;
    private javax.swing.JPanel Attendance;
    private javax.swing.JPanel DeleteEmp;
    private javax.swing.JPanel DeleteEmp1;
    private javax.swing.JPanel Locked;
    private javax.swing.JPanel MainPanel;
    private javax.swing.JPanel MainSection;
    private javax.swing.JPanel Settings;
    private hbtn.HbtnRound btnAttendance;
    private hbtn.HbtnRound btnDeleteScanner;
    private hbtn.HbtnRound btnEmployees;
    private hbtn.HbtnRound btnSettings;
    private NEW.DateChooser dateChooser1;
    private NEW.hTextField hTextField1;
    public static hbtn.HbtnRound hbtnRead;
    private hbtn.HbtnRound hbtnRound1;
    private hbtn.HbtnRound hbtnRound10;
    private hbtn.HbtnRound hbtnRound11;
    private hbtn.HbtnRound hbtnRound12;
    private hbtn.HbtnRound hbtnRound13;
    private hbtn.HbtnRound hbtnRound14;
    private hbtn.HbtnRound hbtnRound16;
    private hbtn.HbtnRound hbtnRound17;
    private hbtn.HbtnRound hbtnRound18;
    private hbtn.HbtnRound hbtnRound2;
    private hbtn.HbtnRound hbtnRound3;
    private hbtn.HbtnRound hbtnRound4;
    private hbtn.HbtnRound hbtnRound5;
    private hbtn.HbtnRound hbtnRound6;
    private hbtn.HbtnRound hbtnRound7;
    private hbtn.HbtnRound hbtnRound8;
    private hbtn.HbtnRound hbtnRound9;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JComboBox<String> jcmbPermanentStatus;
    private javax.swing.JDialog jdAddNewEmp;
    private javax.swing.JDialog jdAddScanner;
    private javax.swing.JLabel jlAbsentNonPer;
    private javax.swing.JLabel jlAbsentPer;
    private javax.swing.JLabel jlAbsentTotal;
    private javax.swing.JLabel jlEmpTotal;
    private javax.swing.JLabel jlErrorAddNewGroup;
    private javax.swing.JLabel jlErrorAddNewScanner;
    public static javax.swing.JLabel jlLoading;
    private javax.swing.JLabel jlNonPerIn;
    private javax.swing.JLabel jlNonPerOut;
    private javax.swing.JLabel jlPerIn;
    private javax.swing.JLabel jlPerOut;
    private javax.swing.JLabel jlPresentNonPer;
    private javax.swing.JLabel jlPresentPer;
    private javax.swing.JLabel jlPresentTotal;
    private javax.swing.JLabel jlStatusDateTime;
    private javax.swing.JLabel jlTotalIn;
    private javax.swing.JLabel jlTotalOut;
    public static javax.swing.JLabel jmMsgTitle1;
    public static javax.swing.JLabel jmMsgTitle2;
    private javax.swing.JPanel jpAddCategory;
    private javax.swing.JPanel jpAddScanner;
    private javax.swing.JPanel jpMain;
    private javax.swing.JPanel jpStatusBar;
    private javax.swing.JTable jtableAttendance;
    private javax.swing.JTable jtableEmployeeList;
    private javax.swing.JTable jtableReadLog;
    private javax.swing.JTable jtableScannerList;
    private javax.swing.JTable jtableUsers;
    private javax.swing.JLabel lblMsg;
    private javax.swing.JPanel navigation;
    private javax.swing.JPanel pnlEmployees;
    private NEW.ShadowPanel shadowPanel1;
    private NEW.ShadowPanel shadowPanel2;
    private NEW.ShadowPanelOriginal shadowPanelOriginal1;
    private NEW.ShadowPanelOriginal shadowPanelOriginal10;
    private NEW.ShadowPanelOriginal shadowPanelOriginal2;
    private NEW.ShadowPanelOriginal shadowPanelOriginal6;
    private NEW.ShadowPanelOriginal shadowPanelOriginal7;
    private NEW.ShadowPanelOriginal shadowPanelOriginal8;
    private NEW.ShadowPanelOriginal shadowPanelOriginal9;
    private static javax.swing.JPasswordField tfPassword;
    private static javax.swing.JTextField tfUserName;
    private javax.swing.JTextField txtAddNewEmpID;
    private javax.swing.JTextField txtEmpEmail;
    private javax.swing.JTextField txtEmpName;
    private javax.swing.JTextField txtEmpPhone;
    private NEW.hTextField txtEmpSearch;
    private javax.swing.JTextField txtScannerID;
    private javax.swing.JTextField txtScannerIP;
    private javax.swing.JTextField txtScannerId;
    private javax.swing.JTextField txtScannerName;
    private javax.swing.JTextField txtScannerNote;
    private javax.swing.JTextField txtScannerPort;
    public static javax.swing.JLabel txtStatus;
    // End of variables declaration//GEN-END:variables
}
