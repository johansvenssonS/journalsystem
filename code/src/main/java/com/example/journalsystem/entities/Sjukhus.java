package com.example.journalsystem.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sjukhus")
public class Sjukhus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    private String namn;
    private String adress;
    private String telefon;


    /// JsonbackReference berättar vad som får serialiseras
    /// använde de när det blev en evightesloop men nu funkar det utan
    @ManyToOne
    @JoinColumn(name = "region_id")
    //@JsonBackReference
    private Region region;


    @OneToMany(mappedBy = "sjukhus")
    private final List<Avdelning> avdelningar = new ArrayList<>();


    public Sjukhus() {
    }

    public String getNamn() {
        return namn;
    }

    public String getAdress() {
        return adress;
    }

    public String getTelefon() {
        return telefon;
    }

    public Region getRegion() {
        return region;

    }

    public Long getId() {
        return id;
    }
}
