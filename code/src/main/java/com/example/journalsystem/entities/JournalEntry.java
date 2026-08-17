package com.example.journalsystem.entities;


import jakarta.persistence.*;

import java.sql.Timestamp;

@jakarta.persistence.Entity
@Table(name = "journal_entry")
public class JournalEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long careContactId;
    private Long createdBy ;
    private Timestamp createdAt;
    private String type;
    private String content;


    public JournalEntry() {
    }

    public Long getId() {
        return id;
    }

    public Long getCareContactId() {
        return careContactId;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }
}
