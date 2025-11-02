package com.apsit.alumni.connect.controller;

import com.apsit.alumni.connect.dto.LoginRequest; // New Import
import com.apsit.alumni.connect.dto.LoginResponse; // New Import
import com.apsit.alumni.connect.dto.RegisterRequest;
import com.apsit.alumni.connect.dto.UserDto;
import com.apsit.alumni.connect.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
// New Import
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // --- REGISTER ENDPOINT (Unchanged) ---
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody RegisterRequest request) {
        UserDto registeredUser = authService.registerUser(request);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    // --- LOGIN ENDPOINT (New) ---
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.loginUser(request);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/test")
public ResponseEntity<String> testSecuredEndpoint() {
    return ResponseEntity.ok("This is a SECURED endpoint!");
}
}