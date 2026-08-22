package com.example.journalsystem.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {
    /// 404- Det som frågades efter finns inte i db.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex){
        ErrorResponse body = new ErrorResponse(
                404,
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(404).body(body);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> alreadyExists(DuplicateResourceException ex){
        ErrorResponse body = new ErrorResponse(
                409,
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(409).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                //error.getField()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        // Instead of a generic "Valideringsfel", combine the individual errors
        // into one readable message, e.g.:
        // "personnummer: must not be blank, email: must be a valid email"
        String combinedMessage = String.join(", ", errors);

        ErrorResponse body = new ErrorResponse(400, combinedMessage, LocalDateTime.now(), errors);
        return ResponseEntity.status(400).body(body);
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        ErrorResponse body = new ErrorResponse(
                403,
                "Du har inte rätt roll/behörighet för att göra detta!",
                LocalDateTime.now()
        );
        return ResponseEntity.status(403).body(body);
    }

}
