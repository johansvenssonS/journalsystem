package com.example.journalsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/// US-62 — mottagande avdelning svarar på en remiss.
public class RespondReferralRequest {

    @NotBlank(message = "status får inte vara tomt")
    @Pattern(regexp = "accepted|declined", message = "status måste vara accepted eller declined")
    private String status;

    @Size(max = 255, message = "response får inte vara längre än 255 tecken")
    private String response;

    public RespondReferralRequest() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
