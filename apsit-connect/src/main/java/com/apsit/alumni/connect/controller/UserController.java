package com.apsit.alumni.connect.controller;

import com.apsit.alumni.connect.dto.UserSummaryDto;
import com.apsit.alumni.connect.model.User; // --- NEW IMPORT ---
import com.apsit.alumni.connect.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // --- NEW IMPORT ---
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // --- UPDATED ENDPOINT ---
    @GetMapping
    public ResponseEntity<List<UserSummaryDto>> getAllUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String branch,
            @AuthenticationPrincipal User currentUser // --- NEW: Get the logged-in user ---
    ) {
        // Pass all params, including the current user's email, to the service
        List<UserSummaryDto> users = userService.getAllUsers(name, branch, currentUser.getEmail());
        return ResponseEntity.ok(users);
    }
}