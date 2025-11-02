package com.apsit.alumni.connect.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private String documentUrl; // --- NEW FIELD ---
}