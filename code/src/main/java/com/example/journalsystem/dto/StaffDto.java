package com.example.journalsystem.dto;

public class StaffDto {

    private Long id;
    private String personalNumber;
    private String firstName;
    private String lastName;

    public StaffDto() {
    }

    public StaffDto(Long id, String personalNumber, String firstName, String lastName) {
        this.id = id;
        this.personalNumber = personalNumber;
        this.firstName = firstName;
        this.lastName = lastName;
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
}
