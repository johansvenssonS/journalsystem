package com.example.journalsystem.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateCareContactRequest {

    @NotNull(message = "patientId får inte vara tomt")
    private Long patientId;

    @NotNull(message = "departmentId får inte vara tomt")
    private Long departmentId;

    @NotNull(message = "responsibleStaffId får inte vara tomt")
    private Long responsibleStaffId;

    @Size(max = 255, message = "Orsak får vara max 255 tecken")
    private String reason;

    public CreateCareContactRequest() {
    }

    public CreateCareContactRequest(Long patientId, Long departmentId, Long responsibleStaffId, String reason) {
        this.patientId = patientId;
        this.departmentId = departmentId;
        this.responsibleStaffId = responsibleStaffId;
        this.reason = reason;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Long getResponsibleStaffId() {
        return responsibleStaffId;
    }

    public void setResponsibleStaffId(Long responsibleStaffId) {
        this.responsibleStaffId = responsibleStaffId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
