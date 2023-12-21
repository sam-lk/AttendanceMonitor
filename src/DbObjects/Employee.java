/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DbObjects;

/**
 *
 * @author harsh
 */
public class Employee {

    private String EmpID = "";
    private String Name = "";
    private String Phone = "";
    private String Email = "";
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

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String Phone) {
        this.Phone = Phone;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getPermanentStatus() {
        return PermanentStatus;
    }

    public void setPermanentStatus(String PermanentStatus) {
        this.PermanentStatus = PermanentStatus;
    }


}
