package com.example.journalsystem.service;

import com.example.journalsystem.dto.CreateJournalEntryRequest;
import com.example.journalsystem.entities.CareContact;
import com.example.journalsystem.entities.JournalEntry;
import com.example.journalsystem.entities.JournalEntryDTO;
import com.example.journalsystem.entities.JournalEntryMapper;
import com.example.journalsystem.exceptions.ResourceNotFoundException;
import com.example.journalsystem.repository.CareContactRepository;
import com.example.journalsystem.repository.JournalRepository;
import com.example.journalsystem.repository.StaffRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;


@Service
public class JournalEntryService {

    public static final String TYPE_NOTE = "note";
    public static final String TYPE_EXAMINATION = "examination";
    public static final String TYPE_OPERATION = "operation";

    public static final String ROLE_DOCTOR = "ROLE_doctor";
    public static final String ROLE_NURSE = "ROLE_nurse";

    // audit_log.event is a fixed MySQL ENUM('read','created','updated','deleted') —
    // the affected table is conveyed separately via the entities lookup row.
    private static final String AUDIT_ENTITY_TYPE = "journal_entry";
    private static final String AUDIT_EVENT_READ = "read";
    private static final String AUDIT_EVENT_CREATED = "created";

    public final JournalRepository journalRepository;
    public final JournalEntryMapper journalEntryMapper;
    private final CareContactRepository careContactRepository;
    private final StaffRepository staffRepository;
    private final AuditLogService auditLogService;
    private final HttpServletRequest httpRequest;

    public JournalEntryService(JournalRepository journalRepository,
                               JournalEntryMapper journalEntryMapper,
                               CareContactRepository careContactRepository,
                               StaffRepository staffRepository,
                               AuditLogService auditLogService,
                               HttpServletRequest httpRequest) {
        this.journalRepository = journalRepository;
        this.journalEntryMapper = journalEntryMapper;
        this.careContactRepository = careContactRepository;
        this.staffRepository = staffRepository;
        this.auditLogService = auditLogService;
        this.httpRequest = httpRequest;
    }

    /// US-50 — varje läsning av en journalpost loggas med tidpunkt och användare.
    public JournalEntryDTO getJournalEntryById(Long id){
        JournalEntry journalEntry = journalRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Journalpost med id: "+id + " Hittades inte"));
        auditLogService.record(AUDIT_EVENT_READ, journalEntry.getCareContact().getPatientId(), AUDIT_ENTITY_TYPE, httpRequest);
        return journalEntryMapper.toDto(journalEntry);
    }

    public List<JournalEntryDTO> getAllJournalEntries(){
        return journalRepository.findAll().stream()
                .map(journalEntryMapper::toDto)
                .toList();
    }

    public List<JournalEntryDTO> getJournalEntriesByPatientId(Long patientId) {
        return journalRepository.findByCareContact_PatientId(patientId)
                .stream()
                .map(journalEntryMapper::toDto)
                .toList();
    }

    /// US-13 — läkare skapar journalpost (note, examination eller operation).
    /// US-22 — sjuksköterska får skapa journalpost, men endast av typen note.
    @Transactional
    public JournalEntryDTO createJournalEntry(CreateJournalEntryRequest request) {

        assertTypeAllowedForCurrentUser(request.getType());

        CareContact careContact = careContactRepository.findById(request.getCareContactId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vårdkontakt med id: " + request.getCareContactId() + " hittades inte"));

        if (!staffRepository.existsById(request.getCreatedBy())) {
            throw new ResourceNotFoundException(
                    "Personal med id: " + request.getCreatedBy() + " hittades inte");
        }

        JournalEntry journalEntry = new JournalEntry();
        journalEntry.setCareContact(careContact);
        journalEntry.setCreatedBy(request.getCreatedBy());
        journalEntry.setType(request.getType());
        journalEntry.setContent(request.getContent());
        journalEntry.setCreatedAt(Timestamp.from(Instant.now()));

        JournalEntry saved = journalRepository.save(journalEntry);
        auditLogService.record(AUDIT_EVENT_CREATED, careContact.getPatientId(), AUDIT_ENTITY_TYPE, httpRequest);
        return journalEntryMapper.toDto(saved);
    }

    /// US-22 — en sjuksköterska som inte också är läkare får bara dokumentera
    /// omvårdnad, alltså journalposter av typen note.
    private void assertTypeAllowedForCurrentUser(String type) {
        if (hasAuthority(ROLE_DOCTOR)) {
            return;
        }
        if (hasAuthority(ROLE_NURSE)) {
            if (!TYPE_NOTE.equals(type)) {
                throw new AccessDeniedException(
                        "Sjuksköterska får endast skapa journalposter av typen " + TYPE_NOTE
                                + ". Typen " + type + " kräver läkarbehörighet.");
            }
            return;
        }
        // Neither doctor nor nurse authority recognized — deny by default.
        throw new AccessDeniedException("Behörighet saknas för att skapa journalposter.");
    }

    private boolean hasAuthority(String authority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        for (GrantedAuthority granted : authentication.getAuthorities()) {
            if (authority.equals(granted.getAuthority())) {
                return true;
            }
        }
        return false;
    }
}
