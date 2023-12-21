/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import DbObjects.Employee;
import DbObjects.JobCard;
import DbObjects.JobTask;
import database.DatabaseConnection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author harsh
 */
public class DbFunctions {

    public Employee checkEmp(String empId) {
        Employee emp = new Employee();

        try {
            ResultSet res1 = DatabaseConnection.Search("SELECT * FROM employee WHERE EmpID = '" + empId + "' ");
            while (res1.next()) {
     
            }

        } catch (SQLException ex) {

        }

        return emp;
    }

    public String checkBarcodeInJobCard(String Barcode) {
        String jobIdAuto = "";
        try {
            ResultSet res1 = DatabaseConnection.Search("SELECT auto_id FROM a_job_card WHERE job_barcode = '" + Barcode + "' ");
            while (res1.next()) {
                jobIdAuto = res1.getString("auto_id");
            }
        } catch (Exception e) {

        }

        return jobIdAuto;
    }

    //Return the first job task which is not closed yet
    public String SearchJobTask(String JobAutoID) {
        String taskAutoId = "";
        try {

            ResultSet res1 = DatabaseConnection.Search("SELECT auto_id FROM a_job_task WHERE job_id = '" + JobAutoID + "' AND Status <> 'CLOSED' Order By auto_id LIMIT 1 ");
            while (res1.next()) {
                taskAutoId = res1.getString("auto_id");
            }
        } catch (Exception e) {

        }

        return taskAutoId;
    }

    //Return the job task for given task id
    public JobTask getJobTask(String TaskAutoID) {
        JobTask jobtsk = new JobTask();
        JobCard jbcard = new JobCard();

        try {
            ResultSet res1 = DatabaseConnection.Search("SELECT * FROM a_job_task WHERE auto_id = '" + TaskAutoID + "' ");
            while (res1.next()) {
                jobtsk.setAutoID(res1.getString("auto_id"));
                jobtsk.setJobAutoID(res1.getString("job_id"));
                jobtsk.setJobTask(res1.getString("job_task"));
                jobtsk.setLocation(res1.getString("Location"));
                jobtsk.setStatus(res1.getString("Status"));
                jobtsk.setStatusNote(res1.getString("Status_Note"));
            }

            ResultSet res2 = DatabaseConnection.Search("SELECT * FROM a_job_card WHERE auto_id = '" + jobtsk.getJobAutoID() + "' ");
            while (res2.next()) {
                jbcard.setAutoID(res2.getString("auto_id"));
                jbcard.setJobNumber(res2.getString("job_Number"));
                jbcard.setCustID(res2.getString("Cust_Id"));
                jbcard.setVehicleType(res2.getString("Vehicle_Type"));
                jbcard.setBrand(res2.getString("Brand"));
                jbcard.setModel(res2.getString("Model"));
                jbcard.setLicensePlate(res2.getString("License_Plate"));
                jbcard.setMilage(res2.getString("Mileage"));
                jbcard.setJobType(res2.getString("Job_Type"));
                jbcard.setBarcode(res2.getString("job_barcode"));
            }

            jobtsk.setJobCard(jbcard);

        } catch (SQLException ex) {

        }

        return jobtsk;
    }

    //Return the job task for given task id
    public JobCard getJobCard(String jobAutoID) {
        JobCard jbcard = new JobCard();

        try {

            ResultSet res2 = DatabaseConnection.Search("SELECT * FROM a_job_card WHERE auto_id = '" + jobAutoID + "' ");
            while (res2.next()) {
                jbcard.setAutoID(res2.getString("auto_id"));
                jbcard.setJobNumber(res2.getString("job_Number"));
                jbcard.setCustID(res2.getString("Cust_Id"));
                jbcard.setVehicleType(res2.getString("Vehicle_Type"));
                jbcard.setBrand(res2.getString("Brand"));
                jbcard.setModel(res2.getString("Model"));
                jbcard.setLicensePlate(res2.getString("License_Plate"));
                jbcard.setMilage(res2.getString("Mileage"));
                jbcard.setJobType(res2.getString("Job_Type"));
                jbcard.setBarcode(res2.getString("job_barcode"));
            }

        } catch (SQLException ex) {

        }

        return jbcard;
    }

    public void UpdateJobTaskAsOpen(String jobTaskID, String EmpID) {
        DatabaseConnection.Update("Update a_job_task Set "
                + "Status = 'STARTED', "
                + "Status_Note = '',"
                + "In_Date='" + MainFrame.SysDate + "', "
                + "In_Time ='" + MainFrame.SysTime + "',"
                + "Emp_Id='" + EmpID + "' "
                + "WHERE auto_id = '" + jobTaskID + "' ");
    }

    public void UpdateJobTaskAsColse(String jobTaskID, String EmpID) {
        DatabaseConnection.Update("Update a_job_task Set "
                + "Status = 'CLOSED', "
                + "Status_Note = '',"
                + "Out_Date='" + MainFrame.SysDate + "', "
                + "Out_Time='" + MainFrame.SysTime + "', "
                + "Emp_Id='" + EmpID + "' "
                + "WHERE auto_id = '" + jobTaskID + "' ");
    }

    public void UpdateJobCard(String JobAuotID, String EmpID, String Status, String Location) {
        DatabaseConnection.Update("Update a_job_card SET "
                + "Assigned_emp_Id ='" + EmpID + "', "
                + "Current_Status = '" + Status + "', "
                + "Current_Location = '" + Location + "' "
                + "WHERE auto_id = '" + JobAuotID + "'  ");
    }
}
