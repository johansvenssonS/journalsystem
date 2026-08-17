package com.example.journalsystem.mapper;

import com.example.journalsystem.entities.AuditLog;
import com.example.journalsystem.dto.AuditLogDTO;
import org.springframework.stereotype.Component;

@Component
public class AuditLogMapper {

    public AuditLogDTO toDto(AuditLog auditLog) {
        if (auditLog == null) {
            return null;
        }

        return new AuditLogDTO(
                auditLog.getId(),
                auditLog.getStaff().getId(),
                auditLog.getPatient().getId(),
                auditLog.getEvent(),
                auditLog.getEntity().getId(),
                auditLog.getOccurredAt(),
                auditLog.getIpAddress()
        );
    }
}