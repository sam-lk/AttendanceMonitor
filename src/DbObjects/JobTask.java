/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DbObjects;

import java.util.Date;

/**
 *
 * @author harsh
 */
public class JobTask {
    private String AutoID = "";
    private String jobAutoID = "";
    private String JobTask = "";
    private String Location = "";
    private String Status = "";
    private String StatusNote = "";
    private String EmpID = "";
    private Date InTime;
    private Date OutTime;
    
    private JobCard jobCard = null;

    public String getAutoID() {
        return AutoID;
    }

    public void setAutoID(String AutoID) {
        this.AutoID = AutoID;
    }

    public String getJobAutoID() {
        return jobAutoID;
    }

    public void setJobAutoID(String AutoID) {
        this.jobAutoID = AutoID;
    }

    public String getJobTask() {
        return JobTask;
    }

    public void setJobTask(String JobTask) {
        this.JobTask = JobTask;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String Location) {
        this.Location = Location;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    public String getStatusNote() {
        return StatusNote;
    }

    public void setStatusNote(String StatusNote) {
        this.StatusNote = StatusNote;
    }

    public String getEmpID() {
        return EmpID;
    }

    public void setEmpID(String EmpID) {
        this.EmpID = EmpID;
    }

    public Date getInTime() {
        return InTime;
    }

    public void setInTime(Date InTime) {
        this.InTime = InTime;
    }

    public Date getOutTime() {
        return OutTime;
    }

    public void setOutTime(Date OutTime) {
        this.OutTime = OutTime;
    }

    public JobCard getJobCard() {
        return jobCard;
    }

    public void setJobCard(JobCard jobCard) {
        this.jobCard = jobCard;
    }
    
    
    
    
}
