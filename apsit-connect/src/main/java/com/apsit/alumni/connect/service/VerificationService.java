package com.apsit.alumni.connect.service;

import com.apsit.alumni.connect.dto.SubmitRequestDto;
import com.apsit.alumni.connect.dto.VerificationRequestDto;
import com.apsit.alumni.connect.model.Role;
import com.apsit.alumni.connect.model.User;
import com.apsit.alumni.connect.model.VerificationRequest;
import com.apsit.alumni.connect.model.VerificationStatus;
import com.apsit.alumni.connect.repository.UserRepository;
import com.apsit.alumni.connect.repository.VerificationRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VerificationService {

    private final VerificationRequestRepository requestRepository;
    private final UserRepository userRepository;

    public VerificationService(VerificationRequestRepository requestRepository, UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
    }

    // Student submit request (unchanged)
    @Transactional
    public VerificationRequestDto submitRequest(String userEmail, SubmitRequestDto submitDto) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found: " + userEmail));

        if (submitDto.getNewEmail() == null || submitDto.getNewEmail().isEmpty()) {
            throw new IllegalArgumentException("New email address is required.");
        }
        if (submitDto.getNewEmail().endsWith("@apsit.edu.in")) {
            throw new IllegalArgumentException("New email cannot be an '@apsit.edu.in' address.");
        }
        if (userRepository.findByEmail(submitDto.getNewEmail()).isPresent()) {
            throw new IllegalArgumentException("The new email address is already in use by another account.");
        }
        
        VerificationRequest newRequest = new VerificationRequest();
        newRequest.setUser(user);
        newRequest.setDocumentUrl(submitDto.getDocumentUrl());
        newRequest.setNewEmail(submitDto.getNewEmail()); 

        VerificationRequest savedRequest = requestRepository.save(newRequest);
        return mapToDto(savedRequest);
    }


    // Admin: Get Pending (unchanged)
    public List<VerificationRequestDto> getAllPendingRequests() {
        List<VerificationRequest> requests = requestRepository.findByStatus(VerificationStatus.PENDING);
        return requests.stream()
                       .map(this::mapToDto)
                       .collect(Collectors.toList());
    }

    // --- UPDATED ADMIN REVIEW METHOD ---
    @Transactional
    public VerificationRequestDto reviewRequest(Long requestId, String adminEmail, boolean isApproved) {
        // 1. Find the request and admin
        VerificationRequest request = requestRepository.findById(requestId)
                 .orElseThrow(() -> new RuntimeException("Request not found"));
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        
        // 2. Update the request
        request.setReviewedBy(admin);
        request.setReviewedAt(LocalDateTime.now());
        
        User userToVerify = request.getUser(); // Get the user (student OR inactive alumnus)

        if (isApproved) {
            request.setStatus(VerificationStatus.APPROVED);

            // --- THIS IS THE FIX ---
            if (request.getNewEmail() != null && !request.getNewEmail().isEmpty()) {
                // SCENARIO 1: Student is upgrading
                // Check if the new email is still available
                if (userRepository.findByEmail(request.getNewEmail()).isPresent() && !userToVerify.getEmail().equals(request.getNewEmail())) {
                     request.setStatus(VerificationStatus.REJECTED);
                     requestRepository.save(request);
                     throw new IllegalArgumentException("Failed to approve: The requested new email " + request.getNewEmail() + " was taken by another user.");
                }
                
                // Update their role AND email
                userToVerify.setRole(Role.ROLE_ALUMNI);
                userToVerify.setEmail(request.getNewEmail());
            } else {
                // SCENARIO 2: New alumnus is being activated
                // No email change, just activate them.
            }
            
            // In both cases, set them to active
            userToVerify.setActive(true);
            userRepository.save(userToVerify); 
            // --- END OF FIX ---
            
        } else {
            request.setStatus(VerificationStatus.REJECTED);
            // If a new alumnus registration is rejected, delete the user
            if (userToVerify.getRole() == Role.ROLE_ALUMNI && !userToVerify.isActive()) {
                userRepository.delete(userToVerify); // This will cascade and delete the request
                return mapToDto(request); // Return DTO *before* request is deleted
            }
        }

        // Save the updated request
        VerificationRequest savedRequest = requestRepository.save(request);
        return mapToDto(savedRequest);
    }

    // Helper mapToDto (unchanged)
    private VerificationRequestDto mapToDto(VerificationRequest request) {
        // ... (this method is unchanged)
        VerificationRequestDto dto = new VerificationRequestDto();
        dto.setId(request.getId());
        dto.setUserId(request.getUser().getId());
        dto.setUserName(request.getUser().getName());
        dto.setUserEmail(request.getUser().getEmail());
        dto.setDocumentUrl(request.getDocumentUrl());
        dto.setStatus(request.getStatus().toString());
        dto.setNewEmail(request.getNewEmail()); 

        if(request.getSubmittedAt() != null) {
             dto.setSubmittedAt(request.getSubmittedAt().toString());
        }
        if (request.getReviewedAt() != null) {
            dto.setReviewedAt(request.getReviewedAt().toString());
        }
        if (request.getReviewedBy() != null) {
            dto.setReviewedByAdminName(request.getReviewedBy().getName());
        }

        return dto;
    }
}