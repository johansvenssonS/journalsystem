package com.example.journalsystem.controller;


import com.example.journalsystem.dto.DepartmentDto;
import com.example.journalsystem.dto.DepartmentPatientDTO;
import com.example.journalsystem.service.DepartmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public ResponseEntity<List<DepartmentDto>> getAll() {
        return
                ResponseEntity.ok(departmentService.getAll());
    }

    @GetMapping("{id}")
    public ResponseEntity<DepartmentDto> getDepartmentById(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.getDepartmentById(id));
    }

    /// US-20 — vårdpersonal ser alla patienter som är inskrivna på avdelningen.
    @PreAuthorize("hasAnyAuthority('doctor','nurse')")
    @GetMapping("/{id}/patients")
    public ResponseEntity<List<DepartmentPatientDTO>> getAdmittedPatients(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.getAdmittedPatients(id));
    }

}
