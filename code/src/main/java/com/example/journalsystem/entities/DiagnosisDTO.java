package com.example.journalsystem.entities;


import jakarta.persistence.Id;

import java.sql.Date;

public class DiagnosisDTO {


    @Id
    private Long id;

    private Long journalEntryId;

    private Long setBy;

    private String icd10Code;

    private String name;

    private String description;
    private Date diagnosedDate;


    public DiagnosisDTO(Long id, Long journalEntryId, Long setBy, String icd10Code, String name, String description, Date diagnosedDate) {
        this.id = id;
        this.journalEntryId = journalEntryId;
        this.setBy = setBy;
        this.icd10Code = icd10Code;
        this.name = name;
        this.description = description;
        this.diagnosedDate = diagnosedDate;
    }

    public Date getDiagnosedDate() {
        return diagnosedDate;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public Long getJournalEntryId() {
        return journalEntryId;
    }

    public String getIcd10Code() {
        return icd10Code;
    }

    public Long getSetBy() {
        return setBy;
    }

    public Long getId() {
        return id;
    }
}
