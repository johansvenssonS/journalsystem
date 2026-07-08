package com.example.journalsystem.controller;

import com.example.journalsystem.dto.StaffDto;
import com.example.journalsystem.repository.StaffRepository;
import com.example.journalsystem.service.StaffService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<StaffDto>> getAll() {
        return
                ResponseEntity.ok(staffService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StaffDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(staffService.getStaffById(id));

    }
}
