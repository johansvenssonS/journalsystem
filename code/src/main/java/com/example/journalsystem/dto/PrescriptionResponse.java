package com.example.journalsystem.dto;

import java.time.LocalDate;

public class PrescriptionResponse {

    private Long id;
    private Long patientId;
    private Long prescribedBy;
    private String medication;
    private String dosage;
    private LocalDate issuedDate;
    private LocalDate endDate;
    private Boolean active;

    public PrescriptionResponse(Long id, Long patientId, Long prescribedBy, String medication,
                                 String dosage, LocalDate issuedDate, LocalDate endDate, Boolean active) {
        this.id = id;
        this.patientId = patientId;
        this.prescribedBy = prescribedBy;
        this.medication = medication;
        this.dosage = dosage;
        this.issuedDate = issuedDate;
        this.endDate = endDate;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public Long getPatientId() {
        return patientId;
    }

    public Long getPrescribedBy() {
        return prescribedBy;
    }

    public String getMedication() {
        return medication;
    }

    public String getDosage() {
        return dosage;
    }

    public LocalDate getIssuedDate() {
        return issuedDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public Boolean getActive() {
        return active;
    }
}
