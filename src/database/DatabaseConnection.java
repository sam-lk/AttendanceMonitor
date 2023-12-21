package database;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import config.SystemSettings;
import helper.SaveErrors;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import javax.swing.JOptionPane;

/**
 *
 * @author Harsha Indunil : Freidea Solution
 */
public class DatabaseConnection {

    public static boolean SQLITE_DB_TYPE; //load from SystemConfiguration class

    public static String SqlHost = "";
    public static String SqlPIN = "";
    public static String SqlPort = "";
    public static String SqlUser = "";
    public static String SqlPath = "";
    public static String SQLiteDbPath = "";
    public static String DatabaseName = "att";

    public static Connection c;
    public static String ErrorMsg = "";
    public static String ProgramDataLocalFolderPath = "";

    public static Connection Connect() {
        System.out.println("DB >> CONFIG FILE PATH:------ " + SystemSettings.ConfigFilePath);
        SQLITE_DB_TYPE = SystemSettings.SQLITE_DB_TYPE;
        ProgramDataLocalFolderPath = System.getenv("ProgramData") + "\\Interithm\\Attendance Monitor\\local";
        
        
        if (SQLITE_DB_TYPE) {
            //SQL LITE
            SQLiteDbPath = ProgramDataLocalFolderPath + "\\att.db";
            System.out.println("Sqlite Mode");

            //check sqlite database file is abalable
            if (!checkSQLiteFile(SQLiteDbPath)) {
                ErrorMsg = "Freidea POS can't start because the SQLite database file is missing from your computer.";
                SaveErrors.SaveErrorLog(new FileNotFoundException("Freidea POS can't start because the SQLite database file is missing from your computer. "));
                return null;
            }

            try {
                if (c == null || c.isClosed()) {
                    Class.forName("org.sqlite.JDBC");
                    c = DriverManager.getConnection("jdbc:sqlite:" + SQLiteDbPath);

                    return c;
                } else {
                    return c;
                }
            } catch (ClassNotFoundException e) {
                ErrorMsg = e.getMessage() + "\n\n- - - - -  Connection Information  - - - - - -\nDatabase : " + SQLiteDbPath;
                SaveErrors.SaveErrorLog(new ClassNotFoundException("Sqlite driver not found. ", e)); //save error
                return null;
            } catch (SQLException e) {
                ErrorMsg = e.getMessage() + "\n\n- - - - -  Connection Information  - - - - - -\nDatabase : " + SQLiteDbPath;
                SaveErrors.SaveErrorLog(new SQLException("Sqlite connection error. ", e)); //save error
                return null;
            }
        } else {
            //MySQL
            System.out.println("MySql Mode");
            try {
                //Check config file is exist
                if (!checkSQLiteFile(SystemSettings.ConfigFilePath)) {
                    ErrorMsg = "Freidea POS can't start because the MySql database config file is missing from your computer.\n\n"
                            + "- - - - - - - Config file information (sample) - - - - - - - -\n"
                            + "SqlPath=C:/Program Files/MySQL/MySQL Server\n"
                            + "SqlUser=root\n"
                            + "SqlHost=192.168.1.1\n"
                            + "SqlPort=3306\n"
                            + "Terminal=FP123\n"
                            + "SaveLocation: " + SQLiteDbPath + "\n"
                            + "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -";
                    SaveErrors.SaveErrorLog(new FileNotFoundException("Freidea POS can't start because the MySql database config file is missing from your computer. "));
                    return null;
                }
                
                if(SystemSettings.Interithm_DB){
                    DatabaseName = "office-pos";
                }
                
                //READ CONFIG FILE
                getMysqlPath();

                if (c == null || c.isClosed()) {
                    //ping 
                    InetAddress address = InetAddress.getByName(SqlHost);
                    boolean reachable = address.isReachable(10000);
                    if (!reachable) {
                        ErrorMsg = "Network connection error. Your host machine \'" + SqlHost + "\' not reachable. \nPlease check your network line, network-switch and host-machine.";
                        SaveErrors.SaveErrorLog(new FileNotFoundException("Network connection error. Your host machine \'" + SqlHost + "\' not reachable. \nPlease check your network line, network-switch and host-machine."));
                        return null;
                    }
                    System.out.println("----------------------- host reachable? " + reachable);
                    System.out.println("DatabaseName: " + DatabaseName);

                    Class.forName("com.mysql.cj.jdbc.Driver");
                    
                    System.out.println("SqlHost: " + SqlHost);
                    System.out.println("SqlPort: " + SqlPort);
                    System.out.println("SqlUser: " + SqlUser);
                    System.out.println("SqlPIN: " + SqlPIN);

                    //c = DriverManager.getConnection("jdbc:mysql://" + SqlHost + ":" + SqlPort + "/" + "data?autoReconnect=true&useUnicode=yes", SqlUser, SqlPIN);
                    c = DriverManager.getConnection("jdbc:mysql://" + SqlHost + ":" + SqlPort + "/" + "" + DatabaseName + "?autoReconnect=true&useUnicode=yes&characterEncoding=UTF-8", SqlUser, SqlPIN);

                    ResultSet res1 = Search("select @@basedir As Path");
                    while (res1.next()) {
                        SqlPath = res1.getString("Path").replace("\\", "/");
                        System.out.println("----------------------- SQL DIR PATH : " + SqlPath);
                    }

                    return c;
                } else {
                    //continue
                    return c;
                }
            } catch (SQLException e) {
                ErrorMsg = e.getMessage() + "\n\n"
                        + "- - - - -  Connection Information  - - - - - -\n"
                        + "Host       : " + SqlHost + "\n"
                        + "Port        : " + SqlPort + "\n"
                        + "User        : " + SqlUser + "\n"
                        + "SqlPath : " + SqlPath + "\n"
                        + "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -";
                SaveErrors.SaveErrorLog(new SQLException("MySql connection error. ", e)); //save error
                //JOptionPane.showMessageDialog(null, "Error: \n" + e.getMessage(), "DataBase Connection Error", JOptionPane.ERROR_MESSAGE);
                return null;
            } catch (ClassNotFoundException e) {
                SaveErrors.SaveErrorLog(new ClassNotFoundException("MySql Driver not found. ", e));
                return null;
            } catch (UnknownHostException e) {
                SaveErrors.SaveErrorLog(new ClassNotFoundException("MySql Unknown Host Exception. ", e));
                return null;
            } catch (IOException e) {
                SaveErrors.SaveErrorLog(new ClassNotFoundException("MySql IO Exception. ", e));
                return null;
            }
        }

    }

    public static boolean checkSQLiteFile(String filePathString) {
        try {
            File f = new File(filePathString);
            if (f.exists() && !f.isDirectory()) {
                return true;
            }
        } catch (Exception e) {
            SaveErrors.SaveErrorLog(e); //save error
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static String CollateNoCase() {
        if (SQLITE_DB_TYPE) {
            return " COLLATE NOCASE ";
        } else {
            return " ";
        }
    }

    public static void Update(String sql) {
        try {
            c.createStatement().executeUpdate(sql);

        } catch (SQLException e) {
            SaveErrors.SaveErrorLog(e); //save error
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: \n" + e.getMessage(), "Database Update Error", JOptionPane.ERROR_MESSAGE);
            try {
                Connect();
                ResultSet r = c.createStatement().executeQuery(sql);
            } catch (SQLException ex) {
                SaveErrors.SaveErrorLog(e);
            }
        }
    }

    public static ResultSet Search(String sql) {
        try {
            ResultSet r = c.createStatement().executeQuery(sql);
            return r;
        } catch (SQLException e) {
            SaveErrors.SaveErrorLog(e); //save error
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: \n" + e.getMessage(), "DataBase Search Error", JOptionPane.ERROR_MESSAGE);
            
            try {
                Connect();
                ResultSet r = c.createStatement().executeQuery(sql);
            } catch (SQLException ex) {
                SaveErrors.SaveErrorLog(e);
            }
            return null;
        }
    }

    private static void getMysqlPath() {
        try {
            //String RootPath = System.getProperty("user.dir");

            Properties prop = new Properties();
            prop.load(new FileInputStream(SystemSettings.ConfigFilePath));
            SqlHost = prop.getProperty("SqlHost");
            //SqlPIN = "1212";
            SqlPIN = "MasterPos1212";
            SqlPort = prop.getProperty("SqlPort");
            SqlUser = prop.getProperty("SqlUser");
            SqlPath = prop.getProperty("SqlPath");

        } catch (Exception ex) {
            SaveErrors.SaveErrorLog(ex);
            ex.printStackTrace();
        }
    }
    
    
    
    public static boolean checkIsConnected(){
        return c != null;
    }
    
    public static String EscChar(String source){
        return source.trim().replace("'", "''");
    }
    
    
    
    
    

//    public static String SaveMySQLSettings(String HOST, String Port, String Username) {
//        String Result = "The SQL server connection successfully created. Please restart application.";
//        try {
//            String FileLoc = SystemSettings.ConfigFilePath;
//            Properties prop = new Properties();
//            prop.load(new FileInputStream(FileLoc));
//
//            prop.setProperty("SqlHost", HOST);
//            prop.setProperty("SqlPort", Port);
//            prop.setProperty("SqlUser", Username);
//
//            prop.store(new FileOutputStream(FileLoc), null);
//
//            return Result;
//        } catch (Exception ex) {
//            SaveErrors.SaveErrorLog(ex);
//            ex.printStackTrace();
//
//            return ex.getMessage();
//        }
//    }

//    public static void SaveInConfigFile(String Terminal, String Branch, String IpAddress) {
//        try {
//            String FileLoc = SystemSettings.ConfigFilePath;
//            if (checkSQLiteFile(FileLoc)) {
//                Properties prop = new Properties();
//                prop.load(new FileInputStream(FileLoc));
//                prop.setProperty("SqlHost", IpAddress);
//                prop.setProperty("Terminal", Terminal);
//                prop.setProperty("Branch", Terminal);
//                prop.store(new FileOutputStream(FileLoc), null);
//            }
//        } catch (Exception e) {
//            SaveErrors.SaveErrorLog(e);
//        }
//    }

    
}
