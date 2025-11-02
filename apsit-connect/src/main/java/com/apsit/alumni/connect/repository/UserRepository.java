package com.apsit.alumni.connect.repository;

import com.apsit.alumni.connect.model.Role;
import com.apsit.alumni.connect.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    
    List<User> findAllByRoleNot(Role role);

    long countByRole(Role role);

    // --- NEW SEARCH METHODS ---

    // Find by name only
    List<User> findByRoleNotAndNameContainingIgnoreCase(Role role, String name);

    // Find by branch only
    List<User> findByRoleNotAndBranchContainingIgnoreCase(Role role, String branch);

    // Find by both name AND branch
    List<User> findByRoleNotAndNameContainingIgnoreCaseAndBranchContainingIgnoreCase(Role role, String name, String branch);
}