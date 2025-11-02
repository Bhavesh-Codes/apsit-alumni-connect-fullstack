package com.apsit.alumni.connect.dto;

import com.apsit.alumni.connect.model.Education;
import com.apsit.alumni.connect.model.Experience;
import lombok.Data;
import java.util.List;

@Data
public class ProfileDto {
    private Long id;
    private String name;
    private String email;
    private String role;

    // Profile Fields
    private String branch;
    private String graduationYear;
    private String title;
    private String company;
    private String location;
    private List<String> skills;
    private String about;
    private String profileImageUrl;

    private List<Experience> experience;
    private List<Education> education;

    // --- NEW FIELD ---
    private Long connectionCount;
}