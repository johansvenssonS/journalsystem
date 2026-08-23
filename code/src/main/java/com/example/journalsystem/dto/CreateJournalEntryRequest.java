package com.example.journalsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class CreateJournalEntryRequest {

    @NotNull(message = "careContactId får inte vara tomt")
    private Long careContactId;

    @NotNull(message = "createdBy får inte vara tomt")
    private Long createdBy;

    @NotBlank(message = "Typ får inte vara tom")
    @Pattern(
            regexp = "note|examination|operation",
            message = "Typ måste vara note, examination eller operation"
    )
    private String type;

    @NotBlank(message = "Innehåll får inte vara tomt")
    private String content;

    public CreateJournalEntryRequest() {
    }

    public CreateJournalEntryRequest(Long careContactId, Long createdBy, String type, String content) {
        this.careContactId = careContactId;
        this.createdBy = createdBy;
        this.type = type;
        this.content = content;
    }

    public Long getCareContactId() {
        return careContactId;
    }

    public void setCareContactId(Long careContactId) {
        this.careContactId = careContactId;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
