package com.apsit.alumni.connect.dto;

import lombok.Data;

@Data
public class VerificationRequestDto {

    private Long id;
    private Long userId;
    private String userName; 
    private String userEmail; // The user's *current* email
    private String documentUrl;
    private String status;
    private String submittedAt;
    private String reviewedAt;
    private String reviewedByAdminName;
    private String newEmail; // --- NEW FIELD ---
}