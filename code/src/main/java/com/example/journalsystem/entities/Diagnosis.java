package com.example.journalsystem.entities;


import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name = "diagnosis")
public class Diagnosis {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long journalEntryId;

    private Long setBy;

    private String icd10Code;

    private String name;

    private String description;

    private Date diagnosedDate;

    public Diagnosis() {
    }

    public Long getId() {
        return id;
    }

    public Long getJournalEntryId() {
        return journalEntryId;
    }

    public Long getSetBy() {
        return setBy;
    }

    public String getIcd10Code() {
        return icd10Code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Date getDiagnosedDate() {
        return diagnosedDate;
    }
}
