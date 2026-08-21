package com.example.journalsystem.entities;


import jakarta.persistence.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@jakarta.persistence.Entity
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

    @OneToMany(mappedBy = "diagnosis")
    private List<JournalEntry> journalEntry = new ArrayList<>();

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

    public void setId(Long id) {
        this.id = id;
    }

    public void setJournalEntryId(Long journalEntryId) {
        this.journalEntryId = journalEntryId;
    }

    public void setSetBy(Long setBy) {
        this.setBy = setBy;
    }

    public void setIcd10Code(String icd10Code) {
        this.icd10Code = icd10Code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDiagnosedDate(Date diagnosedDate) {
        this.diagnosedDate = diagnosedDate;
    }

    public List<JournalEntry> getJournalEntry() {
        return journalEntry;
    }

    public void setJournalEntry(List<JournalEntry> journalEntry) {
        this.journalEntry = journalEntry;
    }
}
