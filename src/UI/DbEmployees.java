/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UI;

import DbObjects.Employee;
import database.DatabaseConnection;

/**
 *
 * @author Harsha
 */
public class DbEmployees {

    public void AddNewEmployee(Employee emp) {
        DatabaseConnection.Update("Insert Into Employees "
                + "(EmpID, "
                + "Name, "
                + "Phone, "
                + "Email, "
                + "PermanentStatus) "
                + "Values ("
                + "'" + emp.getEmpID() + "', "
                + "'" + emp.getName() + "',"
                + "'" + emp.getPhone() + "',"
                + "'" + emp.getEmail() + "',"
                + "'" + emp.getPermanentStatus() + "'  )");

    }

    public void EditEmployee(Employee emp) {
        DatabaseConnection.Update("Update Employees Set "
                + "Name = '" + emp.getName() + "', "
                + "Phone ='" + emp.getPhone() + "', "
                + "Email='" + emp.getEmail() + "', "
                + "PermanentStatus='" + emp.getPermanentStatus() + "' "
                + "Where EmpID='" + emp.getEmpID() + "' ");
    }

    public void DeleteEmployee(String EmpId) {
        DatabaseConnection.Update("Delete Employee Where EmpId='" + EmpId + "'");
    }
}
