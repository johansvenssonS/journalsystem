package com.example.journalsystem.controller;

import com.example.journalsystem.dto.UserAccountDTO;
import com.example.journalsystem.dto.UserAccountRequestDTO;
import com.example.journalsystem.dto.UserAccountResponseDTO;
import com.example.journalsystem.service.UserAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserAccountService userAccountService;

    public AuthController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserAccountResponseDTO> register(@RequestBody UserAccountRequestDTO userAccountRequestDTO) {

        return ResponseEntity.status(201).body(userAccountService.createUserAccount(userAccountRequestDTO));
    }

    @GetMapping("/me")
    public ResponseEntity<String> whoAmI() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        return ResponseEntity.ok(username);
    }
}











