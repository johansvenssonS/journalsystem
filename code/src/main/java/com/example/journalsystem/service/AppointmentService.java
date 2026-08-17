package com.example.journalsystem.service;

import com.example.journalsystem.dto.AppointmentDto;
import com.example.journalsystem.entities.Appointment;
import com.example.journalsystem.exceptions.ResourceNotFoundException;
import com.example.journalsystem.mapper.AppointmentMapper;
import com.example.journalsystem.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;

    public AppointmentService(AppointmentRepository appointmentRepository, AppointmentMapper appointmentMapper) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentMapper = appointmentMapper;
    }

    public List<AppointmentDto> getAll() {
        return appointmentRepository.findAll().stream()
                .map(appointmentMapper::toDto)
                .toList();
    }

    public AppointmentDto getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bokning med id: " + id + " hittades inte"));
        return appointmentMapper.toDto(appointment);
    }

    public List<AppointmentDto> getByStaffIdAndDate(Long staffId, String date) {
        LocalDate requestedDate = LocalDate.parse(date); // YYYY-MM-DD

        return getAll().stream()
                .filter(a -> a.getStaffId() != null && a.getStaffId().equals(staffId))
                .filter(a -> a.getScheduledAt() != null)
                .filter(a -> a.getScheduledAt()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .equals(requestedDate))
                .sorted(Comparator.comparing(AppointmentDto::getScheduledAt))
                .toList();
    }
}

