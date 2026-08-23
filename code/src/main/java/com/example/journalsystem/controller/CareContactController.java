package com.example.journalsystem.controller;

import com.example.journalsystem.dto.CreateCareContactRequest;
import com.example.journalsystem.entities.CareContactDTO;
import com.example.journalsystem.service.CareContactService;
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
public class CareContactController {
    private final CareContactService careContactService;

    public CareContactController(CareContactService careContactService) {
        this.careContactService = careContactService;
    }

    @GetMapping("/vårdkontakter")
    public ResponseEntity<List<CareContactDTO>> getAllCareContacts(){
        return ResponseEntity.ok(careContactService.getAllCareContacts());
    }

    @GetMapping("/vårdkontakter/{id}")
    public ResponseEntity<CareContactDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(careContactService.getCareContactById(id));
    }

    /// US-12 — endast läkare får skapa vårdkontakt.
    @PreAuthorize("hasAuthority('doctor')")
    @PostMapping("/vårdkontakter")
    public ResponseEntity<CareContactDTO> createCareContact(
            @Valid @RequestBody CreateCareContactRequest request) {
        return ResponseEntity.status(201).body(careContactService.createCareContact(request));
    }
}
