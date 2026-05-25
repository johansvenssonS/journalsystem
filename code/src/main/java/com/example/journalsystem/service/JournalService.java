package com.example.journalsystem.service;


import com.example.journalsystem.entities.CareContactDTO;
import com.example.journalsystem.repository.JournalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JournalService {
    public final JournalRepository journalRepository;


    public JournalService(JournalRepository journalRepository) {
        this.journalRepository = journalRepository;
    }

    public List<CareContactDTO> getAllCareContacts(){
        return journalRepository.findAll().stream()
                .map(careContact -> new CareContactDTO(
                        careContact.getId(),
                        careContact.getDepartmentId(),
                        careContact.getResponsibleStaffId(),
                        careContact.getReason(),
                        careContact.getAdmitDate(),
                        careContact.getStatus()
                )).toList();
    }
}
