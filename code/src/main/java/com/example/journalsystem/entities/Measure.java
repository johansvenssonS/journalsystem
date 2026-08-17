package com.example.journalsystem.entities;


import jakarta.persistence.*;

import java.sql.Date;

@jakarta.persistence.Entity
@Table(name = "measure")
public class Measure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long journalEntryId;

    private Long performedBy;

    private String description;

    private Date performedDate;

    public Measure() {
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

    public Date getPerformedAt() {
        return performedDate;
    }
}
