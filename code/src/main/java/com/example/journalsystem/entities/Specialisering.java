package com.example.journalsystem.entities;


import jakarta.persistence.*;

@Entity
@Table(name = "specialisering")
public class Specialisering {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String namn;

    public String namn() {
        return namn;
    }

    public Long getId() {
        return id;
    }

    public Specialisering() {
    }
}
