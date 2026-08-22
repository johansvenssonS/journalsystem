package com.example.journalsystem.dto;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public class CreateAppointmentRequest {

    @NotNull(message = "patientId får inte vara tomt")
    private Long patientId;

    @NotNull(message = "staffId får inte vara tomt")
    private Long staffId;

    @NotNull(message = "departmentId får inte vara tomt")
    private Long departmentId;

    @NotNull(message = "scheduledAt får inte vara tomt")
    private Instant scheduledAt;

    private String note;

    private String status;

    public CreateAppointmentRequest() {
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
}
