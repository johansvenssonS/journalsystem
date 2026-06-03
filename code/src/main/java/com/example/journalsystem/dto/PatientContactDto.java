package com.example.journalsystem.dto;

public class PatientContactDto {
    private Long id;
    private String phone;
    private String email;
    private String address;
    private String emergencyName;
    private String emergencyPhone;

    public PatientContactDto() {

    }

    public PatientContactDto(Long id, String phone, String email, String address, String emergencyName, String emergencyPhone) {
        this.id = id;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.emergencyName = emergencyName;
        this.emergencyPhone = emergencyPhone;
    }

    public String getEmergencyPhone() {
        return emergencyPhone;
    }

    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
    }

    public String getEmergencyName() {
        return emergencyName;
    }

    public void setEmergencyName(String emergencyName) {
        this.emergencyName = emergencyName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
