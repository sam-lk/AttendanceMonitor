/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Attendance;

/**
 *
 * @author Harsha
 */
public class AttendanceTable {
    private String EmpID ="";
    private String Name = "";
    private String Status = "";
    private String PermanentStatus = "";

    public String getEmpID() {
        return EmpID;
    }

    public void setEmpID(String EmpID) {
        this.EmpID = EmpID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    public String getPermanentStatus() {
        return PermanentStatus;
    }

    public void setPermanentStatus(String PermanentStatus) {
        this.PermanentStatus = PermanentStatus;
    }
    
    
}
