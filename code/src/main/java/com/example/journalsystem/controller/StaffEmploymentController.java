// ========== CONTROLLER ==========
package com.example.journalsystem.controller;

import com.example.journalsystem.dto.StaffEmploymentDto;
import com.example.journalsystem.service.StaffEmploymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/staff-employments")
public class StaffEmploymentController {

    private final StaffEmploymentService staffEmploymentService;

    public StaffEmploymentController(StaffEmploymentService staffEmploymentService) {
        this.staffEmploymentService = staffEmploymentService;
    }

    // Get all employments
    @GetMapping
    public ResponseEntity<List<StaffEmploymentDto>> getAll() {
        return ResponseEntity.ok(staffEmploymentService.getAll());
    }
}