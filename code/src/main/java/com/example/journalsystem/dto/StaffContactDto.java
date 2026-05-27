// ========== DTO ==========
package com.example.journalsystem.dto;

public class StaffContactDto {
    private Long id;
    private String email;
    private String phone;
    private String address;

    // Empty Constructor
    public StaffContactDto() {
    }

    // Constructor with all fields
    public StaffContactDto(Long id, String email, String phone, String address) {
        this.id = id;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
