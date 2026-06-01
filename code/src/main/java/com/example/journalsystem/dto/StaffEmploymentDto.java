package com.example.journalsystem.dto;

import java.time.LocalDate;

public class StaffEmploymentDto {
    private Long id;
    private Long staffId;
    private Long roleId;
    private Long departmentId;
    private String licenseNo;
    private LocalDate hiredDate;

    // Empty Constructor
    public StaffEmploymentDto() {
    }

    // Constructor with all fields
    public StaffEmploymentDto(Long id, Long staffId, Long roleId, Long departmentId, String licenseNo, LocalDate hiredDate) {
        this.id = id;
        this.staffId = staffId;
        this.roleId = roleId;
        this.departmentId = departmentId;
        this.licenseNo = licenseNo;
        this.hiredDate = hiredDate;
    }

    // Getters and Setters


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getLicenseNo() {
        return licenseNo;
    }

    public void setLicenseNo(String licenseNo) {
        this.licenseNo = licenseNo;
    }

    public LocalDate getHiredDate() {
        return hiredDate;
    }

    public void setHiredDate(LocalDate hiredDate) {
        this.hiredDate = hiredDate;
    }
}