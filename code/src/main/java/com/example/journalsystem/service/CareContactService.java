package com.example.journalsystem.service;


import com.example.journalsystem.entities.*;
import com.example.journalsystem.exceptions.ResourceNotFoundException;
import com.example.journalsystem.repository.CareContactRepository;
import com.example.journalsystem.repository.JournalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CareContactService {
    public final CareContactRepository careContactRepository;
    public final CareContactMapper careContactMapper;



    public CareContactService(CareContactMapper careContactMapper, JournalEntryMapper journalEntryMapper, CareContactRepository careContactRepository) {
        this.careContactRepository = careContactRepository;
        this.careContactMapper = careContactMapper;

    }

    public List<CareContactDTO> getAllCareContacts(){
        return careContactRepository.findAll().stream()
                .map(careContact -> new CareContactDTO(
                        careContact.getId(),
                        careContact.getDepartmentId(),
                        careContact.getResponsibleStaffId(),
                        careContact.getReason(),
                        careContact.getAdmitDate(),
                        careContact.getStatus()
                )).toList();
    }

    public CareContactDTO getCareContactById(Long id){
        CareContact careContact = careContactRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Vårdkontakt med id: "+ id + " hittades inte"));
        return careContactMapper.toDto(careContact);
    }



}
