package com.example.journalsystem.service;

import com.example.journalsystem.dto.UserAccountRequestDTO;
import com.example.journalsystem.dto.UserAccountResponseDTO;
import com.example.journalsystem.entities.*;
import com.example.journalsystem.exceptions.ResourceNotFoundException;
import com.example.journalsystem.mapper.UserAccountMapper;
import com.example.journalsystem.repository.RoleRepository;
import com.example.journalsystem.repository.DepartmentRepository;

import com.example.journalsystem.repository.UserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserAccountService {
    private final UserAccountRepository userAccountRepository;
    private final UserAccountMapper userAccountMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;


    public UserAccountService(UserAccountRepository userAccountRepository, UserAccountMapper userAccountMapper, PasswordEncoder passwordEncoder, RoleRepository roleRepository, DepartmentRepository departmentRepository) {
        this.userAccountRepository = userAccountRepository;
        this.userAccountMapper = userAccountMapper;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.departmentRepository = departmentRepository;
    }

    // används ej
    // loadUserByUsername i UserDetailsServiceImpl
    public UserAccountResponseDTO getByUsername(String username) {
        UserAccount userAccount = userAccountRepository.findByUsername(username)
                .orElseThrow(()-> new ResourceNotFoundException("Användarnamn" + username + "hittades inte"));
        return userAccountMapper.toDto(userAccount);
    }

    public com.example.journalsystem.dto.CurrentUserDTO getCurrentUserProfile(String username) {
        UserAccount userAccount = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Användarnamn " + username + " hittades inte"));

        String role = null;
        Long staffId = null;
        String firstName = null;
        String lastName = null;
        Long departmentId = null;
        String departmentName = null;

        try {
            Staff staff = userAccount.getStaff();
            staffId = staff.getId();
            firstName = staff.getFirstName();
            lastName = staff.getLastName();

            StaffEmployment employment = staff.getStaffEmployment().get(0);
            role = employment.getRole().getTitle().replaceFirst("^ROLE_", "");
            departmentId = employment.getDepartment().getId();
            departmentName = employment.getDepartment().getName();
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            // Account has no staff/employment profile (e.g. a patient-linked account) — leave those fields null.
        }

        return new com.example.journalsystem.dto.CurrentUserDTO(
                userAccount.getId(),
                userAccount.getUsername(),
                userAccount.getEmail(),
                role,
                staffId,
                firstName,
                lastName,
                departmentId,
                departmentName
        );
    }

    public UserAccountResponseDTO createUserAccount(UserAccountRequestDTO userAccountRequestDTO) {
        UserAccount userAccount = userAccountMapper.toEntity(userAccountRequestDTO);

         userAccount.setPassword(passwordEncoder.encode(userAccount.getPassword()));

        // 1. Fetch the chosen Role by title (e.g., "Läkare")
        Role chosenRole = roleRepository.findByTitle(userAccountRequestDTO.getRoleTitle())
                .orElseThrow(() -> new ResourceNotFoundException("Rollen hittades inte"));

        // 2. Create the Staff profile details
        Staff staff = new Staff();
        staff.setFirstName(userAccountRequestDTO.getFirstName());
        staff.setLastName(userAccountRequestDTO.getLastName());
        staff.setPersonalNumber(userAccountRequestDTO.getPersonalNumber());

        Department chosenDepartment = departmentRepository.findById(userAccountRequestDTO.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Avdelningen hittades inte"));

        // 3. Create the StaffEmployment contract linking Staff to the Role
        StaffEmployment employment = new StaffEmployment();
        employment.setRole(chosenRole);
        employment.setStaff(staff);
        employment.setHiredDate(java.time.LocalDate.now());
        employment.setDepartment(chosenDepartment);

        // 4. Add the contract to the staff employment collection list
        staff.getStaffEmployment().add(employment);

        // 5. Link the parent UserAccount to this new Staff member
        userAccount.setStaff(staff);
        // Explicitly keep patient null since this is a staff account
        userAccount.setPatient(null);



        UserAccount savedAccount = userAccountRepository.save(userAccount);
        return userAccountMapper.toDto(savedAccount);
    }


}



