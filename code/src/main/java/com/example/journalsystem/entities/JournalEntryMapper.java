package com.example.journalsystem.entities;


import org.springframework.stereotype.Component;

@Component
public class JournalEntryMapper {
    public JournalEntryDTO toDto(JournalEntry journalEntry){
        return new JournalEntryDTO(
                journalEntry.getId(),
                journalEntry.getCreatedAt(),
                journalEntry.getType(),
                journalEntry.getContent()
        );
    }
}
