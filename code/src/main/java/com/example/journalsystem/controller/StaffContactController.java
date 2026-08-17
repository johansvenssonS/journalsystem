// ========== CONTROLLER ==========
package com.example.journalsystem.controller;

import com.example.journalsystem.dto.StaffContactDto;
import com.example.journalsystem.service.StaffContactService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/staff-contacts")
public class StaffContactController {

    private final StaffContactService staffContactService;

    public StaffContactController(StaffContactService staffContactService) {
        this.staffContactService = staffContactService;
    }

    // Get all contacts
    @GetMapping
    public ResponseEntity<List<StaffContactDto>> getAll() {
        return ResponseEntity.ok(staffContactService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StaffContactDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(staffContactService.getStaffContactById(id));

    }
}