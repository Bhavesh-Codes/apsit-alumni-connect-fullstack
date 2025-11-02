package com.apsit.alumni.connect.repository;

import com.apsit.alumni.connect.model.Connection;
import com.apsit.alumni.connect.model.ConnectionStatus;
import com.apsit.alumni.connect.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    List<Connection> findByToUserAndStatus(User toUser, ConnectionStatus status);

    boolean existsByFromUserAndToUser(User fromUser, User toUser);
    
    Optional<Connection> findByFromUserAndToUser(User fromUser, User toUser);

    @Query("SELECT c FROM Connection c WHERE (c.fromUser = :user OR c.toUser = :user) AND c.status = :status")
    List<Connection> findAcceptedConnectionsForUser(User user, ConnectionStatus status);
    
    long countByToUserAndStatus(User toUser, ConnectionStatus status);
    
    void deleteAllByFromUserOrToUser(User fromUser, User toUser);

    // --- THIS IS THE FIX ---
    // This was the missing method that AdminService needs
    @Query("SELECT COUNT(c) FROM Connection c WHERE (c.fromUser = :user OR c.toUser = :user) AND c.status = :status")
    long countAcceptedConnectionsForUser(User user, ConnectionStatus status);
    // --- END OF FIX ---
}