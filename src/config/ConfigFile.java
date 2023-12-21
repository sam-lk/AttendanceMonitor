/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package config;

import static database.DatabaseConnection.checkSQLiteFile;
import helper.SaveErrors;
import config.SystemSettings;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 *
 * @author sam
 */
public class ConfigFile {

    String ConfigFileLocation = "";

    public ConfigFile() {
        //
        ConfigFileLocation = System.getenv("ProgramData") + "\\Interithm\\Attendance Monitor\\local\\config_.properties";
    }

    public ConfigFile(String ConfigFilePath) {
        ConfigFileLocation = ConfigFilePath;
    }

    public void SaveMySQLSettings(String HOST, String Port, String Username) throws Exception {

        try {
            //String FileLoc = ConfigFileLocation;
            if (checkSQLiteFile(ConfigFileLocation)) {
                Properties prop = new Properties();
                prop.load(new FileInputStream(ConfigFileLocation));

                String Branch = prop.getProperty("Branch");
                String Terminal = prop.getProperty("Terminal");
                String SqlPath = prop.getProperty("SqlPath");
                String FirstRunMode = prop.getProperty("FirstRunMode");
                if (FirstRunMode == null) {
                    FirstRunMode = "true";
                }

                prop.setProperty("SqlHost", HOST);
                prop.setProperty("SqlPort", Port);
                prop.setProperty("SqlUser", Username);

                prop.setProperty("Branch", Branch);
                prop.setProperty("Terminal", Terminal);
                prop.setProperty("SqlPath", SqlPath);
                prop.setProperty("FirstRunMode", FirstRunMode);

                prop.store(new FileOutputStream(ConfigFileLocation), null);
            } else {
                //create new
                Path path = Paths.get(ConfigFileLocation); //creates Path instance  
                Path p = Files.createFile(path);

                Properties prop = new Properties();
                prop.load(new FileInputStream(ConfigFileLocation));

                prop.setProperty("SqlHost", HOST);
                prop.setProperty("SqlPort", Port);
                prop.setProperty("SqlUser", Username);

                prop.setProperty("Branch", "Default Branch");
                prop.setProperty("Terminal", "Pc-01");
                prop.setProperty("SqlPath", "");
                prop.setProperty("FirstRunMode", "true");

                prop.store(new FileOutputStream(ConfigFileLocation), null);
            }
        } catch (Exception e) {
            SaveErrors.SaveErrorLog(e);
            System.out.println("fff " + e);
        }

    }

    public void SaveTerminalName(String TerminalName) throws Exception {
        //String FileLoc = SystemSettings.ConfigFilePath;
        if (checkSQLiteFile(ConfigFileLocation)) {
            Properties prop = new Properties();
            prop.load(new FileInputStream(ConfigFileLocation));

            String SqlHost = prop.getProperty("SqlHost");
            String SqlPort = prop.getProperty("SqlPort");
            String SqlUser = prop.getProperty("SqlUser");
            String Branch = prop.getProperty("Branch");
            String SqlPath = prop.getProperty("SqlPath");
            String FirstRunMode = prop.getProperty("FirstRunMode");
            if (FirstRunMode == null) {
                FirstRunMode = "true";
            }

            prop.setProperty("SqlHost", SqlHost);
            prop.setProperty("SqlPort", SqlPort);
            prop.setProperty("SqlUser", SqlUser);

            prop.setProperty("Branch", Branch);

            prop.setProperty("Terminal", TerminalName);

            prop.setProperty("SqlPath", SqlPath);
            prop.setProperty("FirstRunMode", FirstRunMode);

            prop.store(new FileOutputStream(ConfigFileLocation), null);
        } else {
            Path path = Paths.get(ConfigFileLocation); //creates Path instance  
            Path p = Files.createFile(path);

            Properties prop = new Properties();
            prop.load(new FileInputStream(ConfigFileLocation));

            prop.setProperty("SqlHost", "localhost");
            prop.setProperty("SqlPort", "3307");
            prop.setProperty("SqlUser", "PosUser");

            prop.setProperty("Branch", "Default Branch");
            prop.setProperty("Terminal", TerminalName);
            prop.setProperty("SqlPath", "");
            prop.setProperty("FirstRunMode", "true");

            prop.store(new FileOutputStream(ConfigFileLocation), null);
        }
    }

    public void SaveBranchlName(String BranchName) throws Exception {
        //String FileLoc = SystemSettings.ConfigFilePath;
        if (checkSQLiteFile(ConfigFileLocation)) {
            Properties prop = new Properties();
            prop.load(new FileInputStream(ConfigFileLocation));

            String SqlHost = prop.getProperty("SqlHost");
            String SqlPort = prop.getProperty("SqlPort");
            String SqlUser = prop.getProperty("SqlUser");
            String Terminal = prop.getProperty("Terminal");
            String SqlPath = prop.getProperty("SqlPath");
            String FirstRunMode = prop.getProperty("FirstRunMode");
            if (FirstRunMode == null) {
                FirstRunMode = "true";
            }

            prop.setProperty("SqlHost", SqlHost);
            prop.setProperty("SqlPort", SqlPort);
            prop.setProperty("SqlUser", SqlUser);

            prop.setProperty("Branch", BranchName);

            prop.setProperty("Terminal", Terminal);
            prop.setProperty("SqlPath", SqlPath);
            prop.setProperty("FirstRunMode", FirstRunMode);

            prop.store(new FileOutputStream(ConfigFileLocation), null);
        } else {
            Path path = Paths.get(ConfigFileLocation); //creates Path instance  
            Path p = Files.createFile(path);

            Properties prop = new Properties();
            prop.load(new FileInputStream(ConfigFileLocation));

            prop.setProperty("SqlHost", "localhost");
            prop.setProperty("SqlPort", "3307");
            prop.setProperty("SqlUser", "PosUser");

            prop.setProperty("Branch", BranchName);
            prop.setProperty("Terminal", "Pc-01");
            prop.setProperty("SqlPath", "");
            prop.setProperty("FirstRunMode", "true");

            prop.store(new FileOutputStream(ConfigFileLocation), null);
        }
    }

    public void SaveHostIpAddress(String IpAddress) throws Exception {
        //String FileLoc = SystemSettings.ConfigFilePath;
        if (checkSQLiteFile(ConfigFileLocation)) {
            Properties prop = new Properties();
            prop.load(new FileInputStream(ConfigFileLocation));

            //String SqlHost = prop.getProperty("SqlHost");
            String SqlPort = prop.getProperty("SqlPort");
            String SqlUser = prop.getProperty("SqlUser");
            String Branch = prop.getProperty("Branch");
            String Terminal = prop.getProperty("Terminal");
            String SqlPath = prop.getProperty("SqlPath");
            String FirstRunMode = prop.getProperty("FirstRunMode");
            if (FirstRunMode == null) {
                FirstRunMode = "true";
            }

            prop.setProperty("SqlHost", IpAddress);

            prop.setProperty("SqlPort", SqlPort);
            prop.setProperty("SqlUser", SqlUser);

            prop.setProperty("Branch", Branch);
            prop.setProperty("Terminal", Terminal);
            prop.setProperty("SqlPath", SqlPath);
            prop.setProperty("FirstRunMode", FirstRunMode);

            prop.store(new FileOutputStream(ConfigFileLocation), null);
        } else {
            Path path = Paths.get(ConfigFileLocation); //creates Path instance  
            Path p = Files.createFile(path);

            Properties prop = new Properties();
            prop.load(new FileInputStream(ConfigFileLocation));

            prop.setProperty("SqlHost", IpAddress);
            prop.setProperty("SqlPort", "3307");
            prop.setProperty("SqlUser", "PosUser");

            prop.setProperty("Branch", "Default Branch");
            prop.setProperty("Terminal", "Pc-01");
            prop.setProperty("SqlPath", "");
            prop.setProperty("FirstRunMode", "true");

            prop.store(new FileOutputStream(ConfigFileLocation), null);
        }
    }

    public void SaveFirstRunMode(String FirstRunMode) throws Exception {
        //String FileLoc = SystemSettings.ConfigFilePath;
        if (checkSQLiteFile(ConfigFileLocation)) {
            Properties prop = new Properties();
            prop.load(new FileInputStream(ConfigFileLocation));

            String SqlHost = prop.getProperty("SqlHost");
            String SqlPort = prop.getProperty("SqlPort");
            String SqlUser = prop.getProperty("SqlUser");
            String Branch = prop.getProperty("Branch");
            String Terminal = prop.getProperty("Terminal");
            String SqlPath = prop.getProperty("SqlPath");

            prop.setProperty("SqlHost", SqlHost);
            prop.setProperty("SqlPort", SqlPort);
            prop.setProperty("SqlUser", SqlUser);

            prop.setProperty("Branch", Branch);
            prop.setProperty("Terminal", Terminal);
            prop.setProperty("SqlPath", SqlPath);

            prop.setProperty("FirstRunMode", FirstRunMode);

            prop.store(new FileOutputStream(ConfigFileLocation), null);
        } else {
            Path path = Paths.get(ConfigFileLocation); //creates Path instance  
            Path p = Files.createFile(path);

            Properties prop = new Properties();
            prop.load(new FileInputStream(ConfigFileLocation));

            prop.setProperty("SqlHost", "localhost");
            prop.setProperty("SqlPort", "3307");
            prop.setProperty("SqlUser", "PosUser");

            prop.setProperty("Branch", "Default Branch");
            prop.setProperty("Terminal", "Pc-01");
            prop.setProperty("SqlPath", "");
            prop.setProperty("FirstRunMode", FirstRunMode);

            prop.store(new FileOutputStream(ConfigFileLocation), null);
        }
    }

    public boolean getFirstRunMode() throws Exception {
        boolean mode = false;
        if (checkSQLiteFile(ConfigFileLocation)) {
            Properties prop = new Properties();
            prop.load(new FileInputStream(ConfigFileLocation));

            String RunModeString = prop.getProperty("FirstRunMode");
            mode = Boolean.parseBoolean(RunModeString);
        } else {
            SaveFirstRunMode("true");
            getFirstRunMode();
        }
        System.out.println("getFirstRunMode(); MODE is : " + mode);
        return mode;
    }
}
