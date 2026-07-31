package com.example.journalsystem.dto;

import java.sql.Date;
import java.time.LocalDate;

public class PrescriptionDTO {
    private Long id;
    private Long patientId;
    private Long prescribedBy;
    private String medication;
    private String dosage;
    private LocalDate issuedDate;
    private LocalDate endDate;
    private Boolean active;

    public PrescriptionDTO(Long id, Long patientId, Long prescribedBy, String medication, String dosage, LocalDate issuedDate, LocalDate endDate, Boolean active) {
        this.id = id;
        this.patientId = patientId;
        this.prescribedBy = prescribedBy;
        this.medication = medication;
        this.dosage = dosage;
        this.issuedDate = issuedDate;
        this.endDate = endDate;
        this.active = active;
    }

    public PrescriptionDTO() {
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