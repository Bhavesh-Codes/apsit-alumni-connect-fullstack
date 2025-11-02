package com.apsit.alumni.connect.dto;

import com.apsit.alumni.connect.model.Education; // New Import
import com.apsit.alumni.connect.model.Experience; // New Import
import lombok.Data;
import java.util.List;

@Data
public class UpdateProfileRequest {
    // We only include fields a user can change
    private String name;
    private String branch;
    private String graduationYear;
    private String title;
    private String company;
    private String location;
    private List<String> skills;
    private String about;
    private String profileImageUrl;

    // --- NEW LISTS ---
    private List<Experience> experience;
    private List<Education> education;
    // --- END NEW LISTS ---
}