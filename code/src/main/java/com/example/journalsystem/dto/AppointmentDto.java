package com.example.journalsystem.dto;

import java.time.Instant;

public class AppointmentDto {
    private Long id;
    private Long patientId;
    private Long staffId;
    private Long departmentId;
    private Instant scheduledAt;
    private String note;
    private String status;
    private Instant updatedAt;

    public AppointmentDto() {
    }

    public AppointmentDto(Long id, Long patientId, Long staffId, Long departmentId, Instant scheduledAt, String note, String status, Instant updatedAt) {
        this.id = id;
        this.patientId = patientId;
        this.staffId = staffId;
        this.departmentId = departmentId;
        this.scheduledAt = scheduledAt;
        this.note = note;
        this.status = status;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Instant getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(Instant scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}

