package com.example.journalsystem.dto;

import java.time.Instant;

public class AppointmentResponse {

    private Long id;
    private Long patientId;
    private Long staffId;
    private Long departmentId;
    private Instant scheduledAt;
    private String note;
    private String status;
    private Instant updatedAt;

    public AppointmentResponse(Long id, Long patientId, Long staffId, Long departmentId,
                                Instant scheduledAt, String note, String status, Instant updatedAt) {
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

    public Long getPatientId() {
        return patientId;
    }

    public Long getStaffId() {
        return staffId;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public Instant getScheduledAt() {
        return scheduledAt;
    }

    public String getNote() {
        return note;
    }

    public String getStatus() {
        return status;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
