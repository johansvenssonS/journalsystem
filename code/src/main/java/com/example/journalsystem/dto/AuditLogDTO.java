package com.example.journalsystem.dto;

import java.time.Instant;

public class AuditLogDTO {
    private Long id;
    private Long staffId;
    private Long patientId;
    private String event;
    private Long entityId;
    private Instant occurredAt;
    private String ipAddress;

    public AuditLogDTO(Long id, Long staffId, Long patientId, String event, Long entityId, Instant occurredAt, String ipAddress) {
        this.id = id;
        this.staffId = staffId;
        this.patientId = patientId;
        this.event = event;
        this.entityId = entityId;
        this.occurredAt = occurredAt;
        this.ipAddress = ipAddress;
    }

    public AuditLogDTO() {
    }

    public Long getId() {
        return id;
    }

    public Long getStaffId() {
        return staffId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public String getEvent() {
        return event;
    }

    public Long getEntityId() {
        return entityId;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public String getIpAddress() {
        return ipAddress;
    }
}