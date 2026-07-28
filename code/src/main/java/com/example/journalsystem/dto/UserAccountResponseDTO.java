package com.example.journalsystem.dto;

public class UserAccountResponseDTO {
    private Long id;
    private String username;

    public UserAccountResponseDTO(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
}