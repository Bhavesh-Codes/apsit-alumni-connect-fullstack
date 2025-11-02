package com.apsit.alumni.connect.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException; // --- NEW IMPORT ---
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Object> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        
        Map<String, Object> body = Map.of(
            "timestamp", System.currentTimeMillis(),
            "status", HttpStatus.BAD_REQUEST.value(),
            "error", "Bad Request",
            "message", ex.getMessage()
        );

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        
        Map<String, Object> body = Map.of(
            "timestamp", System.currentTimeMillis(),
            "status", HttpStatus.BAD_REQUEST.value(),
            "error", "Bad Request",
            "message", ex.getMessage()
        );
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // --- THIS IS THE NEW METHOD ---
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Object> handleDisabledException(DisabledException ex) {
        
        Map<String, Object> body = Map.of(
            "timestamp", System.currentTimeMillis(),
            "status", HttpStatus.FORBIDDEN.value(),
            "error", "Forbidden",
            "message", "Your account is not active. If you are an alumnus, please wait for admin approval."
        );
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }
    // --- END NEW METHOD ---
}