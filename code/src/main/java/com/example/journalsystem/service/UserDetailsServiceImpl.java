package com.example.journalsystem.service;

import com.example.journalsystem.entities.UserAccount;
import com.example.journalsystem.repository.UserAccountRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl  implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    public UserDetailsServiceImpl(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount userAccount = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Användare hittades inte: " + username));

        // Safely extract the role string
        String roleTitle = extractRoleTitle(userAccount);

        return org.springframework.security.core.userdetails.User
                .withUsername(userAccount.getUsername())
                .password(userAccount.getPassword())
                .authorities(roleTitle) // Uses the safely extracted roleTitle!
                .build();
    }

    // Helper method to keep main method clean and handle nulls safely
    private String extractRoleTitle(UserAccount account) {
        try {
            return account.getStaff()
                    .getStaffEmployment()
                    .get(0)
                    .getRole()
                    .getTitle();
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            return "ROLE_USER"; // Default fallback if staff/employment/role is missing
        }
    }



}

