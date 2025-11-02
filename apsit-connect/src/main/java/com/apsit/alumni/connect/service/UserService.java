package com.apsit.alumni.connect.service;

import com.apsit.alumni.connect.dto.UserSummaryDto;
import com.apsit.alumni.connect.model.Connection; // --- NEW IMPORT ---
import com.apsit.alumni.connect.model.Role;
import com.apsit.alumni.connect.model.User;
import com.apsit.alumni.connect.repository.ConnectionRepository; // --- NEW IMPORT ---
import com.apsit.alumni.connect.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional; // --- NEW IMPORT ---
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ConnectionRepository connectionRepository; // --- NEW DEPENDENCY ---

    public UserService(UserRepository userRepository, ConnectionRepository connectionRepository) {
        this.userRepository = userRepository;
        this.connectionRepository = connectionRepository; // --- NEW ---
    }

    // --- UPDATED HELPER METHOD (now takes 2 args) ---
    public UserSummaryDto mapToUserSummaryDto(User user, User currentUser) {
        UserSummaryDto dto = new UserSummaryDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setTitle(user.getTitle());
        dto.setCompany(user.getCompany());
        dto.setLocation(user.getLocation());
        dto.setProfileImageUrl(user.getProfileImageUrl());
        dto.setRole(user.getRole().toString());
        dto.setBranch(user.getBranch());
        
        // --- NEW CONNECTION STATUS LOGIC ---
        // Find connection status *relative to the current user*
        Optional<Connection> c1 = connectionRepository.findByFromUserAndToUser(currentUser, user);
        Optional<Connection> c2 = connectionRepository.findByFromUserAndToUser(user, currentUser);

        if (c1.isPresent() && c1.get().getStatus() == com.apsit.alumni.connect.model.ConnectionStatus.ACCEPTED) {
            dto.setConnectionStatus("ACCEPTED");
        } else if (c2.isPresent() && c2.get().getStatus() == com.apsit.alumni.connect.model.ConnectionStatus.ACCEPTED) {
            dto.setConnectionStatus("ACCEPTED");
        } else if (c1.isPresent() && c1.get().getStatus() == com.apsit.alumni.connect.model.ConnectionStatus.PENDING) {
            dto.setConnectionStatus("PENDING_SENT"); // "I sent them a request"
        } else if (c2.isPresent() && c2.get().getStatus() == com.apsit.alumni.connect.model.ConnectionStatus.PENDING) {
            dto.setConnectionStatus("PENDING_RECEIVED"); // "They sent me a request"
        } else {
            dto.setConnectionStatus("NONE"); // No connection
        }
        // --- END NEW LOGIC ---
        
        return dto;
    }

    // --- UPDATED GET ALL USERS METHOD ---
    public List<UserSummaryDto> getAllUsers(String name, String branch, String currentUserEmail) {
        // 1. Find the user who is searching
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
        
        List<User> users;
        Role adminRole = Role.ROLE_ADMIN;

        // 2. Find all potential users based on search
        if (name != null && !name.isEmpty() && branch != null && !branch.isEmpty()) {
            users = userRepository.findByRoleNotAndNameContainingIgnoreCaseAndBranchContainingIgnoreCase(adminRole, name, branch);
        } else if (name != null && !name.isEmpty()) {
            users = userRepository.findByRoleNotAndNameContainingIgnoreCase(adminRole, name);
        } else if (branch != null && !branch.isEmpty()) {
            users = userRepository.findByRoleNotAndBranchContainingIgnoreCase(adminRole, branch);
        } else {
            users = userRepository.findAllByRoleNot(adminRole);
        }

        // 3. Convert to DTOs, *passing in the currentUser* to check status
        return users.stream()
                .map(user -> mapToUserSummaryDto(user, currentUser)) // Pass both user and currentUser
                .collect(Collectors.toList());
    }
}