package com.example.journalsystem.controller;

import com.example.journalsystem.dto.DepartmentPatientDTO;
import com.example.journalsystem.dto.StaffDTO;
import com.example.journalsystem.service.StaffService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/staff")
public class StaffController {

    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping
    public ResponseEntity<List<StaffDTO>> getAll() {
        return
                ResponseEntity.ok(staffService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StaffDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(staffService.getStaffById(id));

    }

    /// Läkarens/sjuksköterskans egna patienter, dvs. patienter där personen
    /// står som ansvarig på en vårdkontakt — underlag för journalsnabbvyn.
    @PreAuthorize("hasRole('doctor') OR hasRole('nurse')")
    @GetMapping("/{id}/patients")
    public ResponseEntity<List<DepartmentPatientDTO>> getAssignedPatients(@PathVariable Long id) {
        return ResponseEntity.ok(staffService.getAssignedPatients(id));
    }
}
