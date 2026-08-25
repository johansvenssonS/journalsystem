package com.example.journalsystem.dto;

import com.example.journalsystem.entities.Patient;

import java.time.Instant;

public class PatientMedicalDto {
    private Long id;
    private String allergies;
    private String bloodType;

    public PatientMedicalDto() {

    }

    public PatientMedicalDto(Long id, String allergies, String bloodType) {
        this.id = id;
        this.allergies = allergies;
        this.bloodType = bloodType;
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

}
