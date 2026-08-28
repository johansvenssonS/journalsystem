package com.example.journalsystem.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.sql.Date;

@jakarta.persistence.Entity
@Table(name = "diagnosis")
public class Diagnosis {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "journal_entry_id", nullable = false)
    private JournalEntry journalEntry;

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

    public JournalEntry getJournalEntry() {
        return journalEntry;
    }

    public void setJournalEntry(JournalEntry journalEntry) {
        this.journalEntry = journalEntry;
    }

    public Long getJournalEntryId() {
        return journalEntry != null ? journalEntry.getId() : null;
    }

    public Long getSetBy() {
        return setBy;
    }

    public void setSetBy(Long setBy) {
        this.setBy = setBy;
    }

    public String getIcd10Code() {
        return icd10Code;
    }

    public void setIcd10Code(String icd10Code) {
        this.icd10Code = icd10Code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDiagnosedDate() {
        return diagnosedDate;
    }

    public void setDiagnosedDate(Date diagnosedDate) {
        this.diagnosedDate = diagnosedDate;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
