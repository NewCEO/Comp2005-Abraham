package com.example.maternityapi.Models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.Date;


public class Allocation {
    private int id;
    private int admissionID;
    private int employeeID;
    private Date startTime;
    private Date endTime;

    @JsonCreator
    public Allocation(@JsonProperty("id") int id, @JsonProperty("admissionID") int admissionID,
                      @JsonProperty("employeeID") int employeeID, @JsonProperty("startTime") Date startTime,
                      @JsonProperty("endTime") Date endTime) {
        this.id = id;
        this.admissionID = admissionID;
        this.employeeID = employeeID;
        this.startTime = startTime;
        this.endTime = endTime;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAdmissionID() {
        return admissionID;
    }

    public void setAdmissionID(int admissionID) {
        this.admissionID = admissionID;
    }

    public int getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(int employeeID) {
        this.employeeID = employeeID;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
