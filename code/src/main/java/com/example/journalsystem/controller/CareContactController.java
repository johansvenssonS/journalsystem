package com.example.journalsystem.controller;

import com.example.journalsystem.dto.CreateCareContactRequest;
import com.example.journalsystem.dto.DischargeCareContactRequest;
import com.example.journalsystem.entities.CareContactDTO;
import com.example.journalsystem.service.CareContactService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

    @GetMapping("/care-contacts")
    public ResponseEntity<List<CareContactDTO>> getAllCareContacts(){
        return ResponseEntity.ok(careContactService.getAllCareContacts());
    }

    @GetMapping("/care-contacts/{id}")
    public ResponseEntity<CareContactDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(careContactService.getCareContactById(id));
    }

    /// US-12 — receptionisten (admin-rollen i systemet) skapar vårdkontakten vid inskrivning.
    @PreAuthorize("hasRole('RECEPTIONIST')")
    @PostMapping("/care-contacts")
    public ResponseEntity<CareContactDTO> createCareContact(
            @Valid @RequestBody CreateCareContactRequest request) {
        return ResponseEntity.status(201).body(careContactService.createCareContact(request));
    }

    /// US-17 — läkare skriver ut en patient. Bodyn är frivillig; utan den
    /// sätts utskrivningsdatum till nu.
    @PreAuthorize("hasRole('doctor')")
    @PatchMapping("/care-contacts/{id}/discharged")
    public ResponseEntity<CareContactDTO> dischargeCareContact(
            @PathVariable Long id,
            @RequestBody(required = false) DischargeCareContactRequest request) {
        return ResponseEntity.ok(careContactService.dischargeCareContact(id, request));
    }
}
