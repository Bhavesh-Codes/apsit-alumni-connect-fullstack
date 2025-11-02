package com.apsit.alumni.connect.controller;

import com.apsit.alumni.connect.dto.ConnectionDto;
import com.apsit.alumni.connect.dto.UserSummaryDto;
import com.apsit.alumni.connect.model.User;
import com.apsit.alumni.connect.service.ConnectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/connections")
public class ConnectionController {

    private final ConnectionService connectionService;

    public ConnectionController(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    @PostMapping("/send/{toUserId}")
    public ResponseEntity<String> sendRequest(
            @AuthenticationPrincipal User fromUser,
            @PathVariable Long toUserId
    ) {
        String responseMessage = connectionService.sendConnectionRequest(fromUser.getEmail(), toUserId);
        return ResponseEntity.ok(responseMessage);
    }

    @DeleteMapping("/cancel/{toUserId}")
    public ResponseEntity<String> cancelRequest(
            @AuthenticationPrincipal User fromUser,
            @PathVariable Long toUserId
    ) {
        String responseMessage = connectionService.cancelConnectionRequest(fromUser.getEmail(), toUserId);
        return ResponseEntity.ok(responseMessage);
    }

    @GetMapping("/requests/pending")
    public ResponseEntity<List<ConnectionDto>> getPendingRequests(
            @AuthenticationPrincipal User userDetails
    ) {
        List<ConnectionDto> requests = connectionService.getPendingRequests(userDetails.getEmail());
        return ResponseEntity.ok(requests);
    }

    @PostMapping("/requests/respond/{requestId}")
    public ResponseEntity<String> respondToRequest(
            @AuthenticationPrincipal User userDetails,
            @PathVariable Long requestId,
            @RequestBody Map<String, Boolean> response
    ) {
        boolean accept = response.getOrDefault("accept", false);
        String responseMessage = connectionService.respondToConnectionRequest(
                userDetails.getEmail(), 
                requestId, 
                accept
        );
        return ResponseEntity.ok(responseMessage);
    }

    @GetMapping("/my-connections")
    public ResponseEntity<List<UserSummaryDto>> getMyConnections(
            @AuthenticationPrincipal User userDetails
    ) {
        List<UserSummaryDto> connections = connectionService.getMyConnections(userDetails.getEmail());
        return ResponseEntity.ok(connections);
    }

    // --- NEW ENDPOINT TO REMOVE A CONNECTION ---
    @DeleteMapping("/remove/{otherUserId}")
    public ResponseEntity<String> removeConnection(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long otherUserId
    ) {
        String responseMessage = connectionService.removeConnection(currentUser.getEmail(), otherUserId);
        return ResponseEntity.ok(responseMessage);
    }
    // --- END NEW ENDPOINT ---
}