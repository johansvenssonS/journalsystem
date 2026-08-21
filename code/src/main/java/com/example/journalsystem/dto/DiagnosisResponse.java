package com.example.journalsystem.dto;

import java.sql.Date;

public class DiagnosisResponse {

    private Long id;
    private Long journalEntryId;
    private Long setBy;
    private String icd10Code;
    private String name;
    private String description;
    private Date diagnosedDate;

    public DiagnosisResponse(Long id, Long journalEntryId, Long setBy, String icd10Code,
                              String name, String description, Date diagnosedDate) {
        this.id = id;
        this.journalEntryId = journalEntryId;
        this.setBy = setBy;
        this.icd10Code = icd10Code;
        this.name = name;
        this.description = description;
        this.diagnosedDate = diagnosedDate;
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
