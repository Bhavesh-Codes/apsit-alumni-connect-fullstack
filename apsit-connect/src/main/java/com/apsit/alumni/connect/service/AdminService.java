package com.apsit.alumni.connect.service;

import com.apsit.alumni.connect.dto.DashboardStatsDto;
import com.apsit.alumni.connect.dto.ProfileDto;
import com.apsit.alumni.connect.model.ConnectionStatus;
import com.apsit.alumni.connect.model.Role;
import com.apsit.alumni.connect.model.User;
import com.apsit.alumni.connect.model.VerificationStatus;
import com.apsit.alumni.connect.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    // --- DEPENDENCIES UPDATED ---
    private final UserRepository userRepository;
    private final VerificationRequestRepository verificationRepository;
    private final EventRepository eventRepository;
    private final JobRepository jobRepository;
    private final ConnectionRepository connectionRepository;
    private final EventRegistrationRepository eventRegistrationRepository;
    private final NotificationRepository notificationRepository;
    // We no longer need UserService for mapping
    // --- END UPDATED DEPENDENCIES ---

    public AdminService(UserRepository userRepository,
                        VerificationRequestRepository verificationRepository,
                        EventRepository eventRepository,
                        JobRepository jobRepository,
                        ConnectionRepository connectionRepository,
                        EventRegistrationRepository eventRegistrationRepository,
                        NotificationRepository notificationRepository) {
        this.userRepository = userRepository;
        this.verificationRepository = verificationRepository;
        this.eventRepository = eventRepository;
        this.jobRepository = jobRepository;
        this.connectionRepository = connectionRepository;
        this.eventRegistrationRepository = eventRegistrationRepository;
        this.notificationRepository = notificationRepository;
    }

    public DashboardStatsDto getDashboardStats() {
        // ... (this method is unchanged)
        DashboardStatsDto stats = new DashboardStatsDto();
        long totalStudents = userRepository.countByRole(Role.ROLE_STUDENT);
        long totalAlumni = userRepository.countByRole(Role.ROLE_ALUMNI);
        stats.setTotalUsers(totalStudents + totalAlumni); 
        stats.setTotalStudents(totalStudents);
        stats.setTotalAlumni(totalAlumni);
        stats.setPendingVerifications(verificationRepository.countByStatus(VerificationStatus.PENDING));
        stats.setTotalEvents(eventRepository.count());
        stats.setTotalJobs(jobRepository.count());
        return stats;
    }

    // --- NEW HELPER METHOD (Copied from ProfileService) ---
    // This creates the FULL ProfileDto, which is what we need
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
        
        // We can get the connection count for the user
        long count = connectionRepository.countAcceptedConnectionsForUser(user, ConnectionStatus.ACCEPTED);
        dto.setConnectionCount(count);

        return dto;
    }
    // --- END NEW HELPER METHOD ---

    // --- UPDATED FOR SEARCH & MAPPING ---
    public List<ProfileDto> getAllUsersForAdmin(String name, String branch) {
        List<User> users;
        Role adminRole = Role.ROLE_ADMIN;

        // Get all users (excluding admins) based on search
        if (name != null && !name.isEmpty() && branch != null && !branch.isEmpty()) {
            users = userRepository.findByRoleNotAndNameContainingIgnoreCaseAndBranchContainingIgnoreCase(adminRole, name, branch);
        } else if (name != null && !name.isEmpty()) {
            users = userRepository.findByRoleNotAndNameContainingIgnoreCase(adminRole, name);
        } else if (branch != null && !branch.isEmpty()) {
            users = userRepository.findByRoleNotAndBranchContainingIgnoreCase(adminRole, branch);
        } else {
            users = userRepository.findAllByRoleNot(adminRole);
        }

        // --- THIS IS THE FIX ---
        // Use the local, correct mapToProfileDto method
        return users.stream()
                .map(this::mapToProfileDto) // No cast needed
                .collect(Collectors.toList());
        // --- END OF FIX ---
    }
    // --- END UPDATED METHOD ---

    // --- NEW METHOD: DELETE USER (Unchanged) ---
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        if (user.getRole() == Role.ROLE_ADMIN) {
            throw new IllegalArgumentException("Cannot delete an admin user.");
        }
        
        // 1. Delete all "child" records
        jobRepository.deleteAllByPostedBy(user);
        eventRepository.deleteAllByOrganizer(user);
        eventRegistrationRepository.deleteAllByUser(user);
        connectionRepository.deleteAllByFromUserOrToUser(user, user);
        notificationRepository.deleteAllByUser(user);
        verificationRepository.deleteAllByUser(user);

        // 2. Clear ElementCollections
        user.getSkills().clear();
        user.getExperience().clear();
        user.getEducation().clear();
        userRepository.save(user);

        // 3. Finally, delete the user
        userRepository.delete(user);
    }
    // --- END NEW METHOD ---
}