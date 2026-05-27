package com.example.journalsystem.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "department")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "floor", nullable = false)
    private Integer floor;

    @ManyToOne
    @JoinColumn(name = "specialization_id", nullable = false)
    private Specialization specialization;

    // One Department can have many StaffEmployments
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StaffEmployment> staffEmployments;


//
//    // One Department can have many Appointments
//    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Appointment> appointments;
//
//    // One Department can have many Referrals
//    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Referral> referrals;
//
//    // One Department can have many Care Contacts
//    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<CareContact> careContacts;

    public Department() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public Specialization getSpecialization() {
        return specialization;
    }

    public void setSpecialization(Specialization specialization) {
        this.specialization = specialization;
    }

//    public void setStaffEmployments(List<StaffEmployment> staffEmployments) {
//        this.staffEmployments = staffEmployments;
//    }
//
//    public void setAppointments(List<Appointment> appointments) {
//        this.appointments = appointments;
//    }
//
//    public void setReferrals(List<Referral> referrals) {
//        this.referrals = referrals;
//    }
//
//    public void setCareContacts(List<CareContact> careContacts) {
//        this.careContacts = careContacts;
//    }
}
