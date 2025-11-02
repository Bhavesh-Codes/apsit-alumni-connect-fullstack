package com.apsit.alumni.connect.controller;

import com.apsit.alumni.connect.dto.ProfileDto;
import com.apsit.alumni.connect.dto.UpdateProfileRequest;
import com.apsit.alumni.connect.model.User;
import com.apsit.alumni.connect.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
// New import
import org.springframework.web.bind.annotation.GetMapping; 
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/profile") // This is a secured base URL
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    // --- NEW ENDPOINT ---
    @GetMapping("/me")
    public ResponseEntity<ProfileDto> getMyProfile(
            @AuthenticationPrincipal User userDetails
    ) {
        // We get the user's email from the token
        ProfileDto profile = profileService.getMyProfile(userDetails.getEmail());
        return ResponseEntity.ok(profile);
    }
    // --- END NEW ENDPOINT ---

    @PutMapping("/me")
    public ResponseEntity<ProfileDto> updateMyProfile(
            @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal User userDetails
    ) {
        // We get the user's email from the 'userDetails' object (from the JWT)
        ProfileDto updatedProfile = profileService.updateUserProfile(userDetails.getEmail(), request);
        return ResponseEntity.ok(updatedProfile);
    }
}