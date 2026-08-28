package com.example.journalsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.sql.Date;

public class CreateDiagnosisRequest {

    @NotNull(message = "journalEntryId får inte vara tomt")
    private Long journalEntryId;

    @NotNull(message = "setBy får inte vara tomt")
    private Long setBy;

    /// US-24 — nekar diagnoser vars kod inte följer ICD-10-formatet, t.ex. "E11.9".
    @NotBlank(message = "icd10Code får inte vara tomt")
    @Pattern(
            regexp = "^[A-TV-Z][0-9]{2}(\\.[0-9A-Z]{1,4})?$",
            message = "icd10Code måste följa ICD-10-formatet, t.ex. E11.9"
    )
    private String icd10Code;

    @NotBlank(message = "name får inte vara tomt")
    private String name;

    private String description;

    private Date diagnosedDate;

    public CreateDiagnosisRequest() {
    }

    public Long getJournalEntryId() {
        return journalEntryId;
    }

    public void setJournalEntryId(Long journalEntryId) {
        this.journalEntryId = journalEntryId;
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
}
