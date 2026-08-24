package com.example.journalsystem.dto;

import java.sql.Timestamp;

/// US-20 — en patient som just nu är inskriven på en avdelning,
/// tillsammans med uppgifter från den aktiva vårdkontakten.
public class DepartmentPatientDTO {

    private Long patientId;
    private String personalNumber;
    private String firstName;
    private String lastName;
    private Long careContactId;
    private String reason;
    private Timestamp admitDate;

    public DepartmentPatientDTO() {
    }

    public DepartmentPatientDTO(Long patientId, String personalNumber, String firstName,
                                String lastName, Long careContactId, String reason,
                                Timestamp admitDate) {
        this.patientId = patientId;
        this.personalNumber = personalNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.careContactId = careContactId;
        this.reason = reason;
        this.admitDate = admitDate;
    }

    public Long getPatientId() {
        return patientId;
    }

    public String getPersonalNumber() {
        return personalNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Long getCareContactId() {
        return careContactId;
    }

    public String getReason() {
        return reason;
    }

    public Timestamp getAdmitDate() {
        return admitDate;
    }
}
