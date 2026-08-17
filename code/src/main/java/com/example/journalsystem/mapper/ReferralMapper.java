package com.example.journalsystem.mapper;

import com.example.journalsystem.dto.ReferralDTO;
import com.example.journalsystem.entities.Referral;
import org.springframework.stereotype.Component;

@Component
public class ReferralMapper {

    public ReferralDTO toDto(Referral referral) {
        if (referral == null) {
            return null;
        }

        return new ReferralDTO(
                referral.getId(),
                referral.getPatient().getId(),
                referral.getFromDepartment().getId(),
                referral.getToDepartment().getId(),
                referral.getSentBy().getId(),
                referral.getReason(),
                referral.getSentAt(),
                referral.getResponse(),
                referral.getRespondedAt(),
                referral.getStatus()
        );
    }

    public Referral toEntity(ReferralDTO dto) {
        if (dto == null) {
            return null;
        }

        Referral referral = new Referral();
        referral.setReason(dto.getReason());
        referral.setSentAt(dto.getSentAt());
        referral.setResponse(dto.getResponse());
        referral.setRespondedAt(dto.getRespondedAt());
        referral.setStatus(dto.getStatus());

        // resten sker i service (linking Patient, Departments, and SentBy entities)

        return referral;
    }
}