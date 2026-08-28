package com.example.journalsystem.entities;


import jakarta.persistence.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@jakarta.persistence.Entity
@Table(name = "care_contact")
public class CareContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long patientId;

    private long departmentId;

    private long responsibleStaffId;

    private String reason;

    private Timestamp admitDate;

    private Timestamp dischargeDate;

    private String status;

    @OneToMany(mappedBy = "careContact")
    private List<JournalEntry> journalEntry;


    public CareContact() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPatientId() {
        return patientId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(long departmentId) {
        this.departmentId = departmentId;
    }

    public long getResponsibleStaffId() {
        return responsibleStaffId;
    }

    public void setResponsibleStaffId(long responsibleStaffId) {
        this.responsibleStaffId = responsibleStaffId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Timestamp getAdmitDate() {
        return admitDate;
    }

    public void setAdmitDate(Timestamp admitDate) {
        this.admitDate = admitDate;
    }

    public Timestamp getDischargeDate() {
        return dischargeDate;
    }

    public void setDischargeDate(Timestamp dischargeDate) {
        this.dischargeDate = dischargeDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
