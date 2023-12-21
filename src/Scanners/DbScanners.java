/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Scanners;

import database.DatabaseConnection;

/**
 *
 * @author Harsha
 */
public class DbScanners {
    public void AddNewScanner(Scanner scnr) {
        DatabaseConnection.Update("Insert Into Scanners "
                + "(Scanner_Name, "
                + "IP_Address, "
                + "Port, "
                + "Note) "
                + "Values ("
                + "'" + scnr.getName()+ "', "
                + "'" + scnr.getIP() + "',"
                + "'" + scnr.getPort() + "',"
                + "'" + scnr.getNote() + "'  )");

    }

    public void EditScanner(Scanner scnr) {
        DatabaseConnection.Update("Update Scanners Set "
                + "Scanner_Name = '" + scnr.getName() + "', "
                + "IP_Address ='" + scnr.getIP()+ "', "
                + "Port='" + scnr.getPort() + "', "
                + "Note='" + scnr.getNote() + "' "
                + "Where ID='" + scnr.getScannerID() + "' ");
    }

    public void DeleteScanner(String scnrID) {
        DatabaseConnection.Update("Delete From Scanners Where ID='" + scnrID + "'");
    }
}
