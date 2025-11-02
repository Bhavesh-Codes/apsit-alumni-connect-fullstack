package com.apsit.alumni.connect.service;

import com.apsit.alumni.connect.dto.ProfileDto;
import com.apsit.alumni.connect.dto.UpdateProfileRequest;
import com.apsit.alumni.connect.model.ConnectionStatus; // --- NEW IMPORT ---
import com.apsit.alumni.connect.model.User;
import com.apsit.alumni.connect.repository.ConnectionRepository; // --- NEW IMPORT ---
import com.apsit.alumni.connect.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final ConnectionRepository connectionRepository; // --- NEW DEPENDENCY ---

    public ProfileService(UserRepository userRepository, ConnectionRepository connectionRepository) {
        this.userRepository = userRepository;
        this.connectionRepository = connectionRepository; // --- NEW ---
    }

    // --- UPDATED HELPER METHOD ---
    // This now calculates the connection count
    private ProfileDto mapToProfileDto(User user) {
        ProfileDto dto = new ProfileDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().toString());
        dto.setBranch(user.getBranch());
        dto.setGraduationYear(user.getGraduationYear());
        dto.setTitle(user.getTitle());
        dto.setCompany(user.getCompany());
        dto.setLocation(user.getLocation());
        dto.setSkills(user.getSkills());
        dto.setAbout(user.getAbout());
        dto.setProfileImageUrl(user.getProfileImageUrl());
        dto.setExperience(user.getExperience());
        dto.setEducation(user.getEducation());
        
    // --- NEW LOGIC ---
    // Calculate and set the connection count. ConnectionRepository provides
    // findAcceptedConnectionsForUser(...) which returns a List, so use its
    // size() instead of calling a removed count method.
    long count = connectionRepository.findAcceptedConnectionsForUser(user, ConnectionStatus.ACCEPTED)
        .size();
    dto.setConnectionCount(count);
    // --- END NEW LOGIC ---

        return dto;
    }
    // --- END UPDATED METHOD ---

    // --- UPDATED METHOD ---
    // (No change in logic, but it now calls the updated helper)
    public ProfileDto getMyProfile(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
        return mapToProfileDto(user);
    }
    // --- END UPDATED METHOD ---

    // --- UPDATED METHOD ---
    // (No change in logic, but it now calls the updated helper)
    @Transactional 
    public ProfileDto updateUserProfile(String userEmail, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        user.setName(request.getName());
        user.setBranch(request.getBranch());
        user.setGraduationYear(request.getGraduationYear());
        user.setTitle(request.getTitle());
        user.setCompany(request.getCompany());
        user.setLocation(request.getLocation());
        user.setSkills(request.getSkills());
        user.setAbout(request.getAbout());
        user.setProfileImageUrl(request.getProfileImageUrl());

        if (request.getExperience() != null) {
            user.getExperience().clear();
            user.getExperience().addAll(request.getExperience());
        }
        if (request.getEducation() != null) {
            user.getEducation().clear();
            user.getEducation().addAll(request.getEducation());
        }

        User savedUser = userRepository.save(user);
        
        // The helper will now add the connection count automatically
        return mapToProfileDto(savedUser); 
    }
}