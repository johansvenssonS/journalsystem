package com.example.journalsystem.mapper;

import com.example.journalsystem.dto.UserAccountDTO;
import com.example.journalsystem.dto.UserAccountRequestDTO;
import com.example.journalsystem.dto.UserAccountResponseDTO;
import com.example.journalsystem.entities.UserAccount;
import org.springframework.stereotype.Component;

@Component
public class UserAccountMapper {

    public UserAccountResponseDTO toDto(UserAccount userAccount){
        String roleTitle = userAccount.getStaff().getStaffEmployment().get(0).getRole().getTitle();

        return new UserAccountResponseDTO(
                userAccount.getId(),
                userAccount.getUsername()
//                userAccount.getEmail(),
//                userAccount.getPassword(),
//                roleTitle,

        );
    }


    public UserAccount toEntity(UserAccountRequestDTO UserAccountRequestDTO) {
        if (UserAccountRequestDTO == null) {
            return null;
        }

        UserAccount userAccount = new UserAccount();
        userAccount.setUsername(UserAccountRequestDTO.getUsername());
        userAccount.setPassword(UserAccountRequestDTO.getPassword());
        userAccount.setEmail(UserAccountRequestDTO.getEmail());
        //resten sker i service

        return userAccount;
    }
}









