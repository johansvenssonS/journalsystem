package com.example.journalsystem.controller;


import com.example.journalsystem.dto.CreateJournalEntryRequest;
import com.example.journalsystem.entities.JournalEntryDTO;
import com.example.journalsystem.service.JournalEntryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PreAuthorize("hasRole('doctor') OR hasRole('nurse')")
    @GetMapping("journalentry")
    public ResponseEntity<List<JournalEntryDTO>> getAllJournalEntries() {
        return ResponseEntity.ok(journalEntryService.getAllJournalEntries());

    }

    @PreAuthorize("hasRole('doctor') OR hasRole('nurse')")
    @GetMapping("/journalentry/{id}")
    public ResponseEntity<JournalEntryDTO> getJournalEntryById(@PathVariable Long id) {
        return ResponseEntity.ok(journalEntryService.getJournalEntryById(id));
    }

    /// US-13 — läkare skapar journalpost av typen note, examination eller operation.
    /// US-22 — sjuksköterska får skapa journalpost, men typregeln kontrolleras
    /// i servicen som bara tillåter note för den rollen.
    @PreAuthorize("hasRole('doctor') OR hasRole('nurse')")
    @PostMapping("/journalentry")
    public ResponseEntity<JournalEntryDTO> createJournalEntry(
            @Valid @RequestBody CreateJournalEntryRequest request) {
        return ResponseEntity.status(201).body(journalEntryService.createJournalEntry(request));
    }
}
