package com.example.journalsystem.service;

import com.example.journalsystem.entities.JournalEntry;
import com.example.journalsystem.entities.JournalEntryDTO;
import com.example.journalsystem.entities.JournalEntryMapper;
import com.example.journalsystem.exceptions.ResourceNotFoundException;
import com.example.journalsystem.repository.JournalRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class JournalEntryService {


    public final JournalRepository journalRepository;
    public final JournalEntryMapper journalEntryMapper;

    public JournalEntryService(JournalRepository journalRepository, JournalEntryMapper journalEntryMapper) {
        this.journalRepository = journalRepository;
        this.journalEntryMapper = journalEntryMapper;
    }

    public JournalEntryDTO getJournalEntryById(Long id){
        JournalEntry journalEntry = journalRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Journalpost med id: "+id + " Hittades inte"));
        return journalEntryMapper.toDto(journalEntry);
    }

    public List<JournalEntryDTO> getAllJournalEntries(){
        return journalRepository.findAll().stream()
                .map(journalEntry -> new JournalEntryDTO(
                        journalEntry.getId(),
                        journalEntry.getCreatedAt(),
                        journalEntry.getType(),
                        journalEntry.getContent()
                )).toList();
    }
    public List<JournalEntryDTO> findByCareContact_PatientId(Long patientId) {
        return journalRepository.findByCareContact_PatientId(patientId)
                .stream()
                .map(journalEntryMapper::toDto)
                .toList();
    }
}
