package com.example.journalsystem.service;

import com.example.journalsystem.dto.AuditLogDTO;
import com.example.journalsystem.entities.AuditLog;
import com.example.journalsystem.entities.Entity;
import com.example.journalsystem.entities.Staff;
import com.example.journalsystem.entities.UserAccount;
import com.example.journalsystem.exceptions.ResourceNotFoundException;
import com.example.journalsystem.mapper.AuditLogMapper;
import com.example.journalsystem.repository.AuditLogRepository;
import com.example.journalsystem.repository.EntityRepository;
import com.example.journalsystem.repository.PatientRepository;
import com.example.journalsystem.repository.StaffRepository;
import com.example.journalsystem.repository.UserAccountRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class AuditLogService {

    private static final Logger log = LoggerFactory.getLogger(AuditLogService.class);

    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;
    private final EntityRepository entityRepository;
    private final UserAccountRepository userAccountRepository;
    private final StaffRepository staffRepository;
    private final PatientRepository patientRepository;

    public AuditLogService(AuditLogRepository auditLogRepository,
                           AuditLogMapper auditLogMapper,
                           EntityRepository entityRepository,
                           UserAccountRepository userAccountRepository,
                           StaffRepository staffRepository,
                           PatientRepository patientRepository) {
        this.auditLogRepository = auditLogRepository;
        this.auditLogMapper = auditLogMapper;
        this.entityRepository = entityRepository;
        this.userAccountRepository = userAccountRepository;
        this.staffRepository = staffRepository;
        this.patientRepository = patientRepository;
    }

    public List<AuditLogDTO> getAll() {
        return auditLogRepository.findAll().stream()
                .map(auditLogMapper::toDto)
                .toList();
    }

    public AuditLogDTO getAuditLogById(Long id) {
        AuditLog auditLog = auditLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Auditlogg med id: " + id + " hittades inte"));
        return auditLogMapper.toDto(auditLog);
    }

    /// US-53 — administratören ser vem som öppnat en specifik patients journal, och när.
    public List<AuditLogDTO> getByPatientId(Long patientId) {
        return auditLogRepository.findByPatientIdOrderByOccurredAtDesc(patientId).stream()
                .map(auditLogMapper::toDto)
                .toList();
    }

    /// US-50 — loggar vem som läst/ändrat en journalpost, och när.
    /// Auditloggning får aldrig slå ut det faktiska anropet den övervakar,
    /// så fel här fångas och loggas bara istället för att propagera. Körs i en egen
    /// transaktion (REQUIRES_NEW) — annars ärver skrivningen en read-only-transaktion
    /// från t.ex. PatientDashboardService och MySQL-drivrutinen nekar INSERT:en.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(String event, Long patientId, String entityTypeName, HttpServletRequest request) {
        try {
            AuditLog auditLog = new AuditLog();
            resolveCurrentStaffId().ifPresent(staffId -> auditLog.setStaff(staffRepository.getReferenceById(staffId)));
            if (patientId != null) {
                auditLog.setPatient(patientRepository.getReferenceById(patientId));
            }
            auditLog.setEvent(event);
            auditLog.setEntity(resolveEntityType(entityTypeName));
            auditLog.setOccurredAt(Instant.now());
            auditLog.setIpAddress(resolveIpAddress(request));

            auditLogRepository.save(auditLog);
        } catch (RuntimeException e) {
            log.warn("Kunde inte skriva auditlogg för händelse '{}'", event, e);
        }
    }

    private Optional<Long> resolveCurrentStaffId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            return Optional.empty();
        }
        return userAccountRepository.findByUsername(authentication.getName())
                .map(UserAccount::getStaff)
                .filter(Objects::nonNull)
                .map(Staff::getId);
    }

    private Entity resolveEntityType(String entityTypeName) {
        return entityRepository.findByEntity(entityTypeName)
                .orElseGet(() -> {
                    Entity entity = new Entity();
                    entity.setEntity(entityTypeName);
                    return entityRepository.save(entity);
                });
    }

    private String resolveIpAddress(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
