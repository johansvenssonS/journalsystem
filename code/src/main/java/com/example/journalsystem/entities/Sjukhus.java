package com.example.journalsystem.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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



    @ManyToOne
    @JoinColumn(name = "region_id")
    private Region region;


    @OneToMany(mappedBy = "sjukhus", fetch = FetchType.EAGER)
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

    public List<Avdelning> getAvdelningar() {
        return avdelningar;
    }

    public Long getId() {
        return id;
    }
}
