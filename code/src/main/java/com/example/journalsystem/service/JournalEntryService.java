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

    public final JournalRepository journalRepository;
    public final JournalEntryMapper journalEntryMapper;
    private final CareContactRepository careContactRepository;
    private final StaffRepository staffRepository;

    public JournalEntryService(JournalRepository journalRepository,
                               JournalEntryMapper journalEntryMapper,
                               CareContactRepository careContactRepository,
                               StaffRepository staffRepository) {
        this.journalRepository = journalRepository;
        this.journalEntryMapper = journalEntryMapper;
        this.careContactRepository = careContactRepository;
        this.staffRepository = staffRepository;
    }

    public JournalEntryDTO getJournalEntryById(Long id){
        JournalEntry journalEntry = journalRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Journalpost med id: "+id + " Hittades inte"));
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

    /// US-13 — skapar en journalpost (note, examination eller operation)
    /// kopplad till en befintlig vårdkontakt.
    @Transactional
    public JournalEntryDTO createJournalEntry(CreateJournalEntryRequest request) {

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

        return journalEntryMapper.toDto(journalRepository.save(journalEntry));
    }
}
