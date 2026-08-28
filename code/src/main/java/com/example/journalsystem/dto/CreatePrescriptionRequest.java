package com.example.journalsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class CreatePrescriptionRequest {

    @NotNull(message = "patientId får inte vara tomt")
    private Long patientId;

    @NotNull(message = "prescribedBy får inte vara tomt")
    private Long prescribedBy;

    @NotBlank(message = "medication får inte vara tomt")
    @Size(max = 150, message = "medication får inte vara längre än 150 tecken")
    private String medication;

    @Size(max = 100, message = "dosage får inte vara längre än 100 tecken")
    private String dosage;

    private LocalDate issuedDate;

    private LocalDate endDate;

    private Boolean active;

    public CreatePrescriptionRequest() {
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getPrescribedBy() {
        return prescribedBy;
    }

    public void setPrescribedBy(Long prescribedBy) {
        this.prescribedBy = prescribedBy;
    }

    public String getMedication() {
        return medication;
    }

    public void setMedication(String medication) {
        this.medication = medication;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public LocalDate getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(LocalDate issuedDate) {
        this.issuedDate = issuedDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
