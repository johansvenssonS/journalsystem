package com.example.journalsystem.entities;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@jakarta.persistence.Entity
@Table(name = "specialization")
public class Specialization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

   // @OneToMany(mappedBy = "specialization", fetch = FetchType.LAZY)
    //public List<Department> departments;

    @OneToMany(mappedBy = "specialization")
    private Set<Department> departments = new LinkedHashSet<>();

    public Specialization() {
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

    public Set<Department> getDepartments() {
        return departments;
    }

//    public void setDepartments(Set<Department> departments) {
//        this.departments = departments;
//    }

    //public List<Department> getDepartments() {
   //     return departments;
   // }

}
