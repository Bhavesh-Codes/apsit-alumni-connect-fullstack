package com.apsit.alumni.connect.service;

import com.apsit.alumni.connect.dto.ConnectionDto;
import com.apsit.alumni.connect.dto.UserSummaryDto;
import com.apsit.alumni.connect.model.Connection;
import com.apsit.alumni.connect.model.ConnectionStatus;
import com.apsit.alumni.connect.model.NotificationType;
import com.apsit.alumni.connect.model.Role;
import com.apsit.alumni.connect.model.User;
import com.apsit.alumni.connect.repository.ConnectionRepository;
import com.apsit.alumni.connect.repository.NotificationRepository;
import com.apsit.alumni.connect.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
public class ConnectionService {

    private final ConnectionRepository connectionRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    public ConnectionService(ConnectionRepository connectionRepository,
                             UserRepository userRepository,
                             UserService userService,
                             NotificationService notificationService,
                             NotificationRepository notificationRepository) {
        this.connectionRepository = connectionRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.notificationService = notificationService;
        this.notificationRepository = notificationRepository;
    }

    private ConnectionDto mapToConnectionDto(Connection connection, User currentUser) {
        // ... (unchanged)
        ConnectionDto dto = new ConnectionDto();
        dto.setId(connection.getId());
        dto.setStatus(connection.getStatus());
        dto.setCreatedAt(connection.getCreatedAt());
        if (connection.getFromUser().getId().equals(currentUser.getId())) {
            dto.setFromUser(userService.mapToUserSummaryDto(connection.getToUser(), currentUser));
        } else {
            dto.setFromUser(userService.mapToUserSummaryDto(connection.getFromUser(), currentUser));
        }
        return dto;
    }

    @Transactional
    public String sendConnectionRequest(String fromUserEmail, Long toUserId) {
        // ... (unchanged)
        User fromUser = userRepository.findByEmail(fromUserEmail)
                .orElseThrow(() -> new RuntimeException("Sender user not found"));
        User toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new RuntimeException("Receiver user not found"));
        if (toUser.getRole() == Role.ROLE_ADMIN) {
            throw new IllegalArgumentException("Cannot send a connection request to an admin.");
        }
        if (fromUser.getId().equals(toUser.getId())) {
            throw new IllegalArgumentException("Cannot send a connection request to yourself.");
        }
        if (connectionRepository.existsByFromUserAndToUser(fromUser, toUser)) {
            throw new IllegalArgumentException("A connection request has already been sent to this user.");
        }
        if (connectionRepository.existsByFromUserAndToUser(toUser, fromUser)) {
            throw new IllegalArgumentException("This user has already sent you a connection request. Check your inbox!");
        }
        Connection newConnection = new Connection();
        newConnection.setFromUser(fromUser);
        newConnection.setToUser(toUser);
        Connection savedConnection = connectionRepository.save(newConnection);
        String title = "New Connection Request";
        String message = fromUser.getName() + " sent you a connection request.";
        notificationService.createNotification(
            toUser, NotificationType.CONNECTION_REQUEST, title, message, savedConnection.getId()
        );
        return "Connection request sent successfully to " + toUser.getName();
    }
    
    @Transactional
    public String cancelConnectionRequest(String fromUserEmail, Long toUserId) {
        // ... (this method is unchanged)
        User fromUser = userRepository.findByEmail(fromUserEmail)
                .orElseThrow(() -> new RuntimeException("Sender user not found"));
        User toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new RuntimeException("Receiver user not found"));
        Connection request = connectionRepository.findByFromUserAndToUser(fromUser, toUser)
                .orElseThrow(() -> new RuntimeException("Connection request not found."));
        if (request.getStatus() != ConnectionStatus.PENDING) {
            throw new IllegalArgumentException("Cannot cancel a request that is already accepted.");
        }
        connectionRepository.delete(request);
        notificationRepository.deleteByRelatedEntityIdAndType(request.getId(), NotificationType.CONNECTION_REQUEST);
        return "Connection request cancelled.";
    }

    public List<ConnectionDto> getPendingRequests(String userEmail) {
        // ... (this method is unchanged)
        User toUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Connection> requests = connectionRepository.findByToUserAndStatus(toUser, ConnectionStatus.PENDING);
        return requests.stream()
                       .map(req -> mapToConnectionDto(req, toUser))
                       .collect(Collectors.toList());
    }

    @Transactional
    public String respondToConnectionRequest(String userEmail, Long requestId, boolean accept) {
        // ... (this method is unchanged)
        User toUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Connection request = connectionRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Connection request not found"));
        if (!request.getToUser().getId().equals(toUser.getId())) {
            throw new IllegalArgumentException("You are not authorized to respond to this request.");
        }
        if (accept) {
            request.setStatus(ConnectionStatus.ACCEPTED);
            connectionRepository.save(request);
            String title = "Connection Request Accepted";
            String message = toUser.getName() + " accepted your connection request.";
            notificationService.createNotification(
                request.getFromUser(), NotificationType.CONNECTION_ACCEPTED, title, message, request.getId()
            );
            return "Connection request accepted.";
        } else {
            notificationRepository.deleteByRelatedEntityIdAndType(request.getId(), NotificationType.CONNECTION_REQUEST);
            connectionRepository.delete(request);
            return "Connection request rejected.";
        }
    }

    public List<UserSummaryDto> getMyConnections(String userEmail) {
        // ... (this method is unchanged)
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Connection> connections = connectionRepository.findAcceptedConnectionsForUser(currentUser, ConnectionStatus.ACCEPTED);
        List<UserSummaryDto> connectedUsers = new ArrayList<>();
        for (Connection c : connections) {
            if (c.getFromUser().getId().equals(currentUser.getId())) {
                connectedUsers.add(userService.mapToUserSummaryDto(c.getToUser(), currentUser));
            } else {
                connectedUsers.add(userService.mapToUserSummaryDto(c.getFromUser(), currentUser));
            }
        }
        return connectedUsers;
    }

    // --- NEW METHOD TO REMOVE A CONNECTION ---
    @Transactional
    public String removeConnection(String currentUserEmail, Long otherUserId) {
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> new RuntimeException("Other user not found"));

        // Find the connection, regardless of who sent it
        Optional<Connection> c1 = connectionRepository.findByFromUserAndToUser(currentUser, otherUser);
        Optional<Connection> c2 = connectionRepository.findByFromUserAndToUser(otherUser, currentUser);

        Connection connectionToRemove = null;
        if (c1.isPresent()) {
            connectionToRemove = c1.get();
        } else if (c2.isPresent()) {
            connectionToRemove = c2.get();
        } else {
            throw new RuntimeException("Connection not found.");
        }

        // Delete the connection
        connectionRepository.delete(connectionToRemove);
        
        // Delete any notifications related to this (both request and accepted)
        notificationRepository.deleteByRelatedEntityIdAndType(connectionToRemove.getId(), NotificationType.CONNECTION_REQUEST);
        notificationRepository.deleteByRelatedEntityIdAndType(connectionToRemove.getId(), NotificationType.CONNECTION_ACCEPTED);

        return "Connection removed successfully.";
    }
    // --- END NEW METHOD ---
}