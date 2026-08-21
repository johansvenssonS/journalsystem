package com.example.journalsystem.entities;


import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.List;

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

    @OneToMany(mappedBy = "journal_entry")
    private List<CareContact> careContact;

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

    public void setId(Long id) {
        this.id = id;
    }

    public void setCareContactId(Long careContactId) {
        this.careContactId = careContactId;
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

    public List<CareContact> getCareContact() {
        return careContact;
    }

    public void setCareContact(List<CareContact> careContact) {
        this.careContact = careContact;
    }
}
