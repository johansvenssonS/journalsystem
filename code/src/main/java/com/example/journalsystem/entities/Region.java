package com.example.journalsystem.entities;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "region")
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;
    private String namn;

    @Column(name = "organisationsnummer")
    private String organisationsNummer;

    @OneToMany(mappedBy = "region")
    private final List<Sjukhus> sjukhus = new ArrayList<>();


    public Region(Long id, String name, String organisationsNummer) {
        this.id = id;
        this.namn = name;
        this.organisationsNummer = organisationsNummer;
    }

    public Region() {
    }

    public String getOrganisationNumber() {
        return organisationsNummer;
    }

    public Long getId() {
        return id;
    }

    public String getNamn() {
        return namn;
    }
    public List<Sjukhus> getSjukhus() {
        return sjukhus;
    }

}
