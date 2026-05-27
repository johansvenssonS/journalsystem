package com.example.journalsystem.controller;


import com.example.journalsystem.entities.CareContactDTO;
import com.example.journalsystem.service.JournalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
public class JournalController {
    private final JournalService journalService;

    public JournalController(JournalService journalService) {
        this.journalService = journalService;
    }

    @GetMapping("/vårdkontakter")
    public ResponseEntity <List<CareContactDTO>> getAllCareContacts(){
        return ResponseEntity.ok(journalService.getAllCareContacts());
    }

    @GetMapping("/vårdkontakter/{id}")
    public ResponseEntity<CareContactDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(journalService.getById(id));
    }
}
