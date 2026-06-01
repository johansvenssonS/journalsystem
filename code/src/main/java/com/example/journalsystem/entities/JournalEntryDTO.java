package com.example.journalsystem.entities;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.sql.Timestamp;

public class JournalEntryDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Timestamp createdAt;
    private String type;
    private String content;

    public JournalEntryDTO(Long id, Timestamp createdAt, String type, String content) {
        this.id = id;
        this.createdAt = createdAt;
        this.type = type;
        this.content = content;
    }

    public Long getId() {
        return id;
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

