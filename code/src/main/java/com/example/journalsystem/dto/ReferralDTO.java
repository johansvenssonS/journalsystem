package com.example.journalsystem.dto;

import java.time.Instant;

public class ReferralDTO {
    private Long id;
    private Long patientId;
    private Long fromDepartmentId;
    private Long toDepartmentId;
    private Long sentBy;
    private String reason;
    private Instant sentAt;
    private String response;
    private Instant respondedAt;
    private String status;

    public ReferralDTO(Long id, Long patientId, Long fromDepartmentId, Long toDepartmentId, Long sentBy, String reason, Instant sentAt, String response, Instant respondedAt, String status) {
        this.id = id;
        this.patientId = patientId;
        this.fromDepartmentId = fromDepartmentId;
        this.toDepartmentId = toDepartmentId;
        this.sentBy = sentBy;
        this.reason = reason;
        this.sentAt = sentAt;
        this.response = response;
        this.respondedAt = respondedAt;
        this.status = status;
    }

    public ReferralDTO() {
    }


    public Long getId() {
        return id;
    }

    public Long getPatientId() {
        return patientId;
    }

    public Long getFromDepartmentId() {
        return fromDepartmentId;
    }

    public Long getToDepartmentId() {
        return toDepartmentId;
    }

    public Long getSentBy() {
        return sentBy;
    }

    public String getReason() {
        return reason;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public String getResponse() {
        return response;
    }

    public Instant getRespondedAt() {
        return respondedAt;
    }

    public String getStatus() {
        return status;
    }
}