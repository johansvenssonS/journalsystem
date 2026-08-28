package com.example.journalsystem.service;

import com.example.journalsystem.entities.CareContact;
import com.example.journalsystem.repository.CareContactRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/// US-2 och US-51 — avdelningsspärren för patientdata.
///
/// Båda storiesen uttrycker samma regel från olika håll. US-2 säger att
/// personal bara ska se patienter på sin egen avdelning. US-51 säger att
/// åtkomst ska nekas när personalen tillhör en annan avdelning och det
/// inte finns en aktiv vårdkontakt som motiverar den. Regeln blir:
///
///   Personal får se en patients data om patienten har en aktiv vårdkontakt
///   på personalens avdelning.
///
/// Vårdkontakten är alltså både kopplingen och motiveringen. Skrivs patienten
/// ut upphör åtkomsten, vilket är precis vad US-51 efterfrågar.
@Service
public class PatientAccessService {

    /// Status i care_contact som räknas som pågående vård.
    public static final String STATUS_ADMITTED = "admitted";

    /// Roller som arbetar på en avdelning och därför omfattas av spärren.
    /// Receptionisten undantas — hen behöver nå patienter i hela huset för
    /// registrering och bokning, och hindras från medicinsk data av US-42.
    private static final List<String> CLINICAL_AUTHORITIES =
            List.of("ROLE_doctor", "ROLE_nurse", "ROLE_assistant_nurse");

    private final CurrentUserService currentUserService;
    private final CareContactRepository careContactRepository;

    public PatientAccessService(CurrentUserService currentUserService,
                                CareContactRepository careContactRepository) {
        this.currentUserService = currentUserService;
        this.careContactRepository = careContactRepository;
    }

    /// True om den inloggade tillhör en roll som är knuten till en avdelning.
    public boolean isClinicalUser() {
        return CLINICAL_AUTHORITIES.stream().anyMatch(currentUserService::hasAuthority);
    }

    /// Får den inloggade se den här patientens data?
    @Transactional(readOnly = true)
    public boolean canAccessPatient(Long patientId) {
        if (patientId == null) {
            return false;
        }
        if (!isClinicalUser()) {
            return true;
        }
        Long departmentId = currentUserService.getDepartmentId().orElse(null);
        if (departmentId == null) {
            return false;
        }
        return careContactRepository.existsByPatientIdAndDepartmentIdAndStatus(
                patientId, departmentId, STATUS_ADMITTED);
    }

    /// Kastar AccessDeniedException om patienten ligger utanför avdelningen.
    /// GlobalExceptionHandler översätter det till 403 med förklarande text.
    @Transactional(readOnly = true)
    public void assertCanAccessPatient(Long patientId) {
        if (!canAccessPatient(patientId)) {
            throw new AccessDeniedException(
                    "Patienten är inte inskriven på din avdelning och du har ingen "
                            + "aktiv vårdkontakt som motiverar åtkomst.");
        }
    }

    /// Id för de patienter den inloggade får se. Tomt betyder "ingen
    /// begränsning" och används inte — anropare ska kolla isClinicalUser först.
    @Transactional(readOnly = true)
    public List<Long> accessiblePatientIds() {
        Long departmentId = currentUserService.getDepartmentId().orElse(null);
        if (departmentId == null) {
            return List.of();
        }
        return careContactRepository
                .findByDepartmentIdAndStatus(departmentId, STATUS_ADMITTED)
                .stream()
                .map(CareContact::getPatientId)
                .distinct()
                .toList();
    }
}
