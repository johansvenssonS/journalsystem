package com.example.journalsystem.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.sql.Timestamp;
import java.util.List;

@jakarta.persistence.Entity
@Table(name = "journal_entry")
public class JournalEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "care_contact_id", nullable = false)
    private CareContact careContact;

    private Long createdBy ;
    private Timestamp createdAt;
    private String type;
    private String content;

    public JournalEntry() {
    }

    public Long getId() {
        return id;
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCareContact(CareContact careContact) {
        this.careContact = careContact;
    }

    public CareContact getCareContact() {
        return careContact;
    }
}
