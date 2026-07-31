package com.example.journalsystem.service;

import com.example.journalsystem.dto.ReferralDTO;
import com.example.journalsystem.entities.Referral;
import com.example.journalsystem.exceptions.ResourceNotFoundException;
import com.example.journalsystem.mapper.ReferralMapper;
import com.example.journalsystem.repository.ReferralRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReferralService {

    private final ReferralRepository referralRepository;
    private final ReferralMapper referralMapper;

    public ReferralService(ReferralRepository referralRepository,
                           ReferralMapper referralMapper) {
        this.referralRepository = referralRepository;
        this.referralMapper = referralMapper;
    }

    public List<ReferralDTO> getAll() {
        return referralRepository.findAll().stream()
                .map(referralMapper::toDto)
                .toList();
    }

    public ReferralDTO getReferralById(Long id) {
        Referral referral = referralRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Remiss med id: " + id + " hittades inte"));
        return referralMapper.toDto(referral);
    }
}