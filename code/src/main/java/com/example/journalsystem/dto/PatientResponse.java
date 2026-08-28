package com.example.journalsystem.dto;

import java.time.Instant;

public class PatientResponse {
    private Long id;
    private String personalNumber;
    private String firstName;
    private String lastName;
    private Instant deletedAt;

    public PatientResponse() {
    }

    public PatientResponse(Long id, String personalNumber, String firstName, String lastName, Instant deletedAt) {
        this.id = id;
        this.personalNumber = personalNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.deletedAt = deletedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPersonalNumber() {
        return personalNumber;
    }

    public void setPersonalNumber(String personalNumber) {
        this.personalNumber = personalNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }
}
