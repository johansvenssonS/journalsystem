package com.example.journalsystem.dto;

import com.example.journalsystem.entities.Patient;

import java.time.Instant;

public class PatientMedicalDto {
    private Long id;
    private String allergies;
    private String bloodType;
    private Instant createdAt;

    public PatientMedicalDto() {

    }

    public PatientMedicalDto(Long id, String address, String allergies, Instant createdAt) {
        this.id = id;
        this.allergies = allergies;
        this.bloodType = bloodType;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
