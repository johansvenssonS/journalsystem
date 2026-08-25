package com.example.journalsystem.controller;

import com.example.journalsystem.dto.CreateReferralRequest;
import com.example.journalsystem.dto.ReferralDTO;
import com.example.journalsystem.dto.ReferralResponse;
import com.example.journalsystem.dto.RespondReferralRequest;
import com.example.journalsystem.service.ReferralService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/referrals")
public class ReferralController {

    private final ReferralService referralService;

    public ReferralController(ReferralService referralService) {
        this.referralService = referralService;
    }

    @GetMapping
    public ResponseEntity<List<ReferralDTO>> getAll() {
        return ResponseEntity.ok(referralService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReferralDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(referralService.getReferralById(id));
    }

    @PreAuthorize("hasRole('doctor')")
    @PostMapping
    public ResponseEntity<ReferralResponse> createReferral(@Valid @RequestBody CreateReferralRequest createReferralRequest) {
        return ResponseEntity.status(201).body(referralService.createReferral(createReferralRequest));
    }

    /// US-62 — mottagande avdelning svarar på en remiss.
    @PreAuthorize("hasRole('doctor')")
    @PatchMapping("/{id}/respond")
    public ResponseEntity<ReferralDTO> respondToReferral(
            @PathVariable Long id,
            @Valid @RequestBody RespondReferralRequest respondReferralRequest) {
        return ResponseEntity.ok(referralService.respondToReferral(id, respondReferralRequest));
    }
}