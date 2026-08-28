package com.example.journalsystem.entities;

import java.sql.Date;

public class MeasureDTO {
    private Long id;
    private Long journalEntryId;
    private Long performedBy;
    private String description;
    private Date performedDate;


    public MeasureDTO(Long id, Long journalEntryId, Long performedBy, String description, Date performedDate) {
        this.id = id;
        this.journalEntryId = journalEntryId;
        this.performedBy = performedBy;
        this.description = description;
        this.performedDate = performedDate;
    }

    public Long getId() {
        return id;
    }

    public Long getJournalEntryId() {
        return journalEntryId;
    }

    public Long getPerformedBy() {
        return performedBy;
    }

    public String getDescription() {
        return description;
    }

    public Date getPerformedDate() {
        return performedDate;
    }
}
