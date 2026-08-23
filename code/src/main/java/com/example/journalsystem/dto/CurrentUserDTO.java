package com.example.journalsystem.dto;

public class CurrentUserDTO {

    private Long id;
    private String username;
    private String email;
    private String role;
    private Long staffId;
    private String firstName;
    private String lastName;
    private Long departmentId;
    private String departmentName;

    public CurrentUserDTO() {
    }

    public CurrentUserDTO(Long id, String username, String email, String role, Long staffId,
                           String firstName, String lastName, Long departmentId, String departmentName) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.staffId = staffId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public Long getStaffId() {
        return staffId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }
}
