package com.example.journalsystem.mapper;

import com.example.journalsystem.entities.AuditLog;
import com.example.journalsystem.entities.Patient;
import com.example.journalsystem.entities.Staff;
import com.example.journalsystem.dto.AuditLogDTO;
import org.springframework.stereotype.Component;

@Component
public class AuditLogMapper {

    public AuditLogDTO toDto(AuditLog auditLog) {
        if (auditLog == null) {
            return null;
        }

        Staff staff = auditLog.getStaff();
        Patient patient = auditLog.getPatient();

        return new AuditLogDTO(
                auditLog.getId(),
                staff != null ? staff.getId() : null,
                staff != null ? staff.getFirstName() + " " + staff.getLastName() : null,
                patient != null ? patient.getId() : null,
                patient != null ? patient.getFirstName() + " " + patient.getLastName() : null,
                auditLog.getEvent(),
                auditLog.getEntity() != null ? auditLog.getEntity().getId() : null,
                auditLog.getEntity() != null ? auditLog.getEntity().getEntity() : null,
                auditLog.getOccurredAt(),
                auditLog.getIpAddress()
        );
    }
}