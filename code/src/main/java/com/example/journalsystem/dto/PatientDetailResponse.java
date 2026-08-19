package com.example.journalsystem.dto;

import java.time.Instant;

public class PatientDetailResponse {
    // Basic Patient Info
    private Long id;
    private String personalNumber;
    private String firstName;
    private String lastName;
    private Instant deletedAt;

    // Contact Info
    private String phone;
    private String email;
    private String address;
    private String emergencyName;
    private String emergencyPhone;

    // Constructors, Getters, and Setters

    public PatientDetailResponse(Long id, String personalNumber, String firstName, String lastName, Instant deletedAt, String phone, String email, String address, String emergencyName, String emergencyPhone) {
        this.id = id;
        this.personalNumber = personalNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.deletedAt = deletedAt;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.emergencyName = emergencyName;
        this.emergencyPhone = emergencyPhone;
    }

    public PatientDetailResponse() {
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmergencyName() {
        return emergencyName;
    }

    public void setEmergencyName(String emergencyName) {
        this.emergencyName = emergencyName;
    }

    public String getEmergencyPhone() {
        return emergencyPhone;
    }

    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
    }
}
