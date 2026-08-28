package com.example.journalsystem.exceptions;


import java.time.LocalDateTime;
import java.util.List;

public class ErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timestamp;
    private List<String> errors;

    public ErrorResponse(int status, String message, LocalDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }
    public ErrorResponse(int status, String message, LocalDateTime timestamp, List<String> errors) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
        this.errors = errors;
    }


    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public List<String> getErrors() {
        return errors;
    }
}
