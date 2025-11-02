package com.apsit.alumni.connect.controller;

import com.apsit.alumni.connect.dto.SubmitRequestDto;
import com.apsit.alumni.connect.dto.VerificationRequestDto;
import com.apsit.alumni.connect.model.User;
import com.apsit.alumni.connect.service.VerificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// --- NEW IMPORT ---
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/verification") 
public class VerificationController {

    private final VerificationService verificationService;

    public VerificationController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    // --- STUDENT ENDPOINT (No change) ---
    @PostMapping("/request")
    public ResponseEntity<VerificationRequestDto> submitVerificationRequest(
            @AuthenticationPrincipal User userDetails,
            @RequestBody SubmitRequestDto submitDto
    ) {
        String userEmail = userDetails.getEmail();
        VerificationRequestDto newRequest = verificationService.submitRequest(userEmail, submitDto);
        return new ResponseEntity<>(newRequest, HttpStatus.CREATED);
    }

    // --- ADMIN ENDPOINT 1 (Secured) ---
    @GetMapping("/admin/pending")
    @PreAuthorize("hasRole('ADMIN')") // <-- LOCK
    public ResponseEntity<List<VerificationRequestDto>> getPendingRequests() {
        List<VerificationRequestDto> requests = verificationService.getAllPendingRequests();
        return ResponseEntity.ok(requests);
    }

    // --- ADMIN ENDPOINT 2 (Secured) ---
    @PostMapping("/admin/review/{requestId}")
    @PreAuthorize("hasRole('ADMIN')") // <-- LOCK
    public ResponseEntity<VerificationRequestDto> reviewRequest(
            @PathVariable Long requestId,
            @RequestBody Map<String, Boolean> review,
            @AuthenticationPrincipal User adminDetails
    ) {
        boolean isApproved = review.getOrDefault("isApproved", false);
        String adminEmail = adminDetails.getEmail();

        VerificationRequestDto updatedRequest = verificationService.reviewRequest(requestId, adminEmail, isApproved);
        return ResponseEntity.ok(updatedRequest);
    }
}