package com.example.journalsystem.entities;


import jakarta.persistence.*;
///klass för avdelningstabell
@Entity ///tabell med namn avdelning
@Table(name = "avdelning")
public class Avdelning {
    @Id/// autoincremental id  Pk
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String namn;

    private int vaningsplan;

    /// avdelning har specicialisering akutvård etc. ta in de här
    @ManyToOne
    @JoinColumn(name = "specialisering_id")
    private Specialisering specialisering;
    @ManyToOne
    @JoinColumn(name = "sjukhus_id")
    private Sjukhus sjukhus;


    public Sjukhus getSjukhus() {
        return sjukhus;
    }

    public Specialisering getSpecialisering() {
        return specialisering;
    }

    public int getVaningsplan() {
        return vaningsplan;
    }
    public String getNamn() {
        return namn;
    }
}
