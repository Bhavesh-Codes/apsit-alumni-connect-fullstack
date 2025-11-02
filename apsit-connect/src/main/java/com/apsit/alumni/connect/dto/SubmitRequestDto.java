package com.apsit.alumni.connect.dto;

import lombok.Data;

@Data
public class SubmitRequestDto {
    private String documentUrl;
    private String newEmail; // --- NEW FIELD ---
}