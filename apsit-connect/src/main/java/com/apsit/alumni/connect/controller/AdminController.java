package com.apsit.alumni.connect.controller;

import com.apsit.alumni.connect.dto.DashboardStatsDto;
import com.apsit.alumni.connect.dto.ProfileDto;
import com.apsit.alumni.connect.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*; // --- NEW IMPORT ---

import java.util.List;
import java.util.Map; // --- NEW IMPORT ---

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardStatsDto> getDashboardStats() {
        DashboardStatsDto stats = adminService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    // --- UPDATED ENDPOINT (FOR SEARCH) ---
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProfileDto>> getAllUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String branch
    ) {
        List<ProfileDto> users = adminService.getAllUsersForAdmin(name, branch);
        return ResponseEntity.ok(users);
    }
    // --- END UPDATED ENDPOINT ---

    // --- NEW ENDPOINT (FOR DELETE) ---
    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteUser(
            @PathVariable Long userId
    ) {
        adminService.deleteUser(userId);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully."));
    }
    // --- END NEW ENDPOINT ---
}