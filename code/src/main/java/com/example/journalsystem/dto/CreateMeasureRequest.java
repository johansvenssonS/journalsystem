package com.example.journalsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.sql.Date;

public class CreateMeasureRequest {

    @NotNull(message = "journalEntryId får inte vara tomt")
    private Long journalEntryId;

    @NotNull(message = "performedBy får inte vara tomt")
    private Long performedBy;

    @NotBlank(message = "description får inte vara tomt")
    @Size(max = 255, message = "description får inte vara längre än 255 tecken")
    private String description;

    private Date performedDate;

    public CreateMeasureRequest() {
    }

    public Long getJournalEntryId() {
        return journalEntryId;
    }

    public void setJournalEntryId(Long journalEntryId) {
        this.journalEntryId = journalEntryId;
    }

    public Long getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(Long performedBy) {
        this.performedBy = performedBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getPerformedDate() {
        return performedDate;
    }

    public void setPerformedDate(Date performedDate) {
        this.performedDate = performedDate;
    }
}
