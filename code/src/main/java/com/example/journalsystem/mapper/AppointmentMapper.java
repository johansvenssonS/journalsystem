package com.example.journalsystem.mapper;

import com.example.journalsystem.dto.AppointmentDto;
import com.example.journalsystem.entities.Appointment;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    public AppointmentDto toDto(Appointment appointment) {
        return new AppointmentDto(
                appointment.getId(),
                appointment.getPatient() != null ? appointment.getPatient().getId() : null,
                appointment.getStaff() != null ? appointment.getStaff().getId() : null,
                appointment.getDepartment() != null ? appointment.getDepartment().getId() : null,
                appointment.getScheduledAt(),
                appointment.getNote(),
                appointment.getStatus(),
                appointment.getUpdatedAt()
        );
    }
}

