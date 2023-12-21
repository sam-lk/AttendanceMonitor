/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Employees;

import Employees.Employee;
import database.DatabaseConnection;

/**
 *
 * @author Harsha
 */
public class DbEmployees {

    public void AddNewEmployee(Employee emp) {
        System.out.println("emp.getEmpID(): " + emp.getEmpID());
        DatabaseConnection.Update("Insert Into Employees "
                + "(EmpID, "
                + "ScannerID, "
                + "Name, "
                + "Phone, "
                + "Email, "
                + "PermanentStatus) "
                + "Values ("
                + "'" + emp.getEmpID().trim() + "', "
                + "'" + emp.getScannerID().trim()+ "', "
                + "'" + emp.getName() + "',"
                + "'" + emp.getPhone() + "',"
                + "'" + emp.getEmail() + "',"
                + "'" + emp.getPermanentStatus() + "'  )");

    }

    public void EditEmployee(Employee emp) {
        DatabaseConnection.Update("Update Employees Set "
                + "ScannerID = '" + emp.getScannerID().trim()+ "', "
                + "Name = '" + emp.getName() + "', "
                + "Phone ='" + emp.getPhone() + "', "
                + "Email='" + emp.getEmail() + "', "
                + "PermanentStatus='" + emp.getPermanentStatus() + "' "
                + "Where EmpID='" + emp.getEmpID().trim() + "' ");
    }

    public void DeleteEmployee(String EmpId) {
        DatabaseConnection.Update("Delete From Employees Where EmpId='" + EmpId.trim() + "'");
    }
}
