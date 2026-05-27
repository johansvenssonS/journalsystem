package com.example.journalsystem.service;


import com.example.journalsystem.entities.CareContact;
import com.example.journalsystem.entities.CareContactDTO;
import com.example.journalsystem.entities.CareContactMapper;
import com.example.journalsystem.exceptions.ResourceNotFoundException;
import com.example.journalsystem.repository.JournalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JournalService {
    public final JournalRepository journalRepository;
    public final CareContactMapper careContactMapper;


    public JournalService(JournalRepository journalRepository, CareContactMapper careContactMapper) {
        this.journalRepository = journalRepository;
        this.careContactMapper = careContactMapper;
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

    public CareContactDTO getById(Long id){
        CareContact careContact = journalRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Vårdkontakt med id: "+ id + "hittades inte"));
        return careContactMapper.toDto(careContact);
    }

}
