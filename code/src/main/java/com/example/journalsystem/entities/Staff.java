package com.example.journalsystem.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@jakarta.persistence.Entity
@Table(name = "staff")
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "personal_number", nullable = false, length = 13)
    private String personalNumber;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @OneToMany(mappedBy = "staff")
    private List<StaffContact> staffContact;

    @OneToMany(mappedBy = "staff", cascade = CascadeType.ALL)
    private List<StaffEmployment> staffEmployment = new ArrayList<>(); // <-- Initialize here!

    public Staff() {
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

    public List<StaffContact> getStaffContact() {
        return staffContact;
    }

    public List<StaffEmployment> getStaffEmployment() {
        return staffEmployment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
