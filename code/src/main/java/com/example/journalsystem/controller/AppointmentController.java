package com.example.journalsystem.controller;

import com.example.journalsystem.dto.AppointmentDto;
import com.example.journalsystem.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public ResponseEntity<List<AppointmentDto>> getAll() {
        return ResponseEntity.ok(appointmentService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    @GetMapping("/schedule")
    public ResponseEntity<List<AppointmentDto>> getSchedule(
            @RequestParam Long staffId,
            @RequestParam String date // format: YYYY-MM-DD
    ) {
        return ResponseEntity.ok(appointmentService.getByStaffIdAndDate(staffId, date));
    }
}

