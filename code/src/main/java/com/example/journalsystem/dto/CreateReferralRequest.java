package com.example.journalsystem.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public class CreateReferralRequest {

    @NotNull(message = "patientId får inte vara tomt")
    private Long patientId;

    @NotNull(message = "fromDepartmentId får inte vara tomt")
    private Long fromDepartmentId;

    @NotNull(message = "toDepartmentId får inte vara tomt")
    private Long toDepartmentId;

    @NotNull(message = "sentBy får inte vara tomt")
    private Long sentBy;

    @Size(max = 255, message = "reason får inte vara längre än 255 tecken")
    private String reason;

    private Instant sentAt;

    public CreateReferralRequest() {
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getFromDepartmentId() {
        return fromDepartmentId;
    }

    public void setFromDepartmentId(Long fromDepartmentId) {
        this.fromDepartmentId = fromDepartmentId;
    }

    public Long getToDepartmentId() {
        return toDepartmentId;
    }

    public void setToDepartmentId(Long toDepartmentId) {
        this.toDepartmentId = toDepartmentId;
    }

    public Long getSentBy() {
        return sentBy;
    }

    public void setSentBy(Long sentBy) {
        this.sentBy = sentBy;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }
}
