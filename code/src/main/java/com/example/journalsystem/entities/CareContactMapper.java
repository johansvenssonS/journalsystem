package com.example.journalsystem.entities;


import org.springframework.stereotype.Component;

@Component
public class CareContactMapper {
    public CareContactDTO toDto(CareContact careContact){
        return new CareContactDTO(
                careContact.getId(),
                careContact.getDepartmentId(),
                careContact.getResponsibleStaffId(),
                careContact.getReason(),
                careContact.getAdmitDate(),
                careContact.getStatus()
        );
    }

    ///public CareContact toEntity()
}
