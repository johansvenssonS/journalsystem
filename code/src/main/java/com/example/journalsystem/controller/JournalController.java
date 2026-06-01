package com.example.journalsystem.controller;


import com.example.journalsystem.entities.JournalEntryDTO;
import com.example.journalsystem.service.JournalEntryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
public class JournalController {

    private final JournalEntryService journalEntryService;

    public JournalController(JournalEntryService journalEntryService) {
        this.journalEntryService = journalEntryService;
    }


    @GetMapping("journalentry")
    public ResponseEntity<List<JournalEntryDTO>> getAllJournalEntries() {
        return ResponseEntity.ok(journalEntryService.getAllJournalEntries());

    }

    @GetMapping("/journalentry/{id}")
    public ResponseEntity<JournalEntryDTO> getJournalEntryById(@PathVariable Long id) {
        return ResponseEntity.ok(journalEntryService.getJournalEntryById(id));
    }
}
