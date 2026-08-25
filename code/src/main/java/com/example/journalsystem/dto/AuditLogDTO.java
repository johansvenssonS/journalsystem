package com.example.journalsystem.dto;

import java.time.Instant;

public class AuditLogDTO {
    private Long id;
    private Long staffId;
    private String staffName;
    private Long patientId;
    private String patientName;
    private String event;
    private Long entityId;
    private String entityType;
    private Instant occurredAt;
    private String ipAddress;

    public AuditLogDTO(Long id, Long staffId, String staffName, Long patientId, String patientName,
                        String event, Long entityId, String entityType, Instant occurredAt, String ipAddress) {
        this.id = id;
        this.staffId = staffId;
        this.staffName = staffName;
        this.patientId = patientId;
        this.patientName = patientName;
        this.event = event;
        this.entityId = entityId;
        this.entityType = entityType;
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

    public String getStaffName() {
        return staffName;
    }

    public Long getPatientId() {
        return patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getEvent() {
        return event;
    }

    public Long getEntityId() {
        return entityId;
    }

    public String getEntityType() {
        return entityType;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public String getIpAddress() {
        return ipAddress;
    }
}