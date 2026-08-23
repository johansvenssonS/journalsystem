package com.example.journalsystem.controller;

import com.example.journalsystem.dto.CurrentUserDTO;
import com.example.journalsystem.dto.LoginRequest;
import com.example.journalsystem.dto.LoginResponse;
import com.example.journalsystem.dto.UserAccountRequestDTO;
import com.example.journalsystem.dto.UserAccountResponseDTO;
import com.example.journalsystem.security.JwtService;
import com.example.journalsystem.service.UserAccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserAccountService userAccountService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(UserAccountService userAccountService, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userAccountService = userAccountService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserAccountResponseDTO> register(@RequestBody UserAccountRequestDTO userAccountRequestDTO) {
        return ResponseEntity.status(201).body(userAccountService.createUserAccount(userAccountRequestDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(Object::toString)
                .orElse("ROLE_USER");

        String token = jwtService.generateToken(authentication.getName(), role);
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @GetMapping("/me")
    public ResponseEntity<CurrentUserDTO> whoAmI() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        return ResponseEntity.ok(userAccountService.getCurrentUserProfile(username));
    }
}
