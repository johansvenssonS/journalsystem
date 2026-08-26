package com.example.journalsystem.dto;

import jakarta.validation.constraints.*;

public class UserAccountRequestDTO {

    @NotBlank(message = "Användarnamn får inte vara tomt")
    @Size(min = 3, max = 50, message = "Användarnamn måste vara mellan 3 och 50 tecken")
    private String username;

    @NotBlank(message = "Lösenord får inte vara tomt")
    @Size(min = 8, max = 100, message = "Lösenordet måste vara minst 8 tecken")
    private String password;

    @NotBlank(message = "E-post får inte vara tom")
    @Email(message = "Ogiltig e-postadress")
    @Size(max = 100, message = "E-postadressen får max vara 100 tecken")
    private String email;

    @NotBlank(message = "Förnamn får inte vara tomt")
    @Size(max = 100, message = "Förnamnet får max vara 100 tecken")
    private String firstName;

    @NotBlank(message = "Efternamn får inte vara tomt")
    @Size(max = 100, message = "Efternamnet får max vara 100 tecken")
    private String lastName;

    @Pattern(regexp = "^\\d{6,8}-?\\d{4}$", message = "Personnumret måste vara i formatet ÅÅMMDD-XXXX eller ÅÅÅÅMMDD-XXXX")
    private String personalNumber;

    @NotBlank(message = "Roll måste anges")
    private String roleTitle;

    @NotNull(message = "Avdelning måste anges")
    private Long departmentId;

    public UserAccountRequestDTO() {
    }

    public UserAccountRequestDTO(String username, String password, String email, String firstName, String lastName, String personalNumber, String roleTitle, Long departmentId) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.personalNumber = personalNumber;
        this.roleTitle = roleTitle;
        this.departmentId = departmentId;
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

    public String getPersonalNumber() {
        return personalNumber;
    }

    public void setPersonalNumber(String personalNumber) {
        this.personalNumber = personalNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRoleTitle() {
        return roleTitle;
    }

    public void setRoleTitle(String roleTitle) {
        this.roleTitle = roleTitle;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }
}