package com.example.journalsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CreatePatientRequest {

    @NotBlank(message = "Personnummer får inte vara tomt")
    @Pattern(
            regexp = "^(19|20)?\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])[-+]?\\d{4}$",
            message = "Ogiltigt personnummer"
    )
    private String personalNumber;

    @NotBlank(message = "Förnamn får inte vara tomt")
    private String firstName;

    @NotBlank(message = "Efternamn får inte vara tomt")
    private String lastName;

    public CreatePatientRequest() {
    }

    public CreatePatientRequest(String personalNumber, String firstName, String lastName) {
        this.personalNumber = personalNumber;
        this.firstName = firstName;
        this.lastName = lastName;
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
