package com.apsit.alumni.connect.dto;

import lombok.Data;

@Data
public class UserSummaryDto {
    private Long id;
    private String name;
    private String title;
    private String company;
    private String location;
    private String profileImageUrl;
    private String role;
    private String branch;
    private String connectionStatus; // --- NEW FIELD ---
}