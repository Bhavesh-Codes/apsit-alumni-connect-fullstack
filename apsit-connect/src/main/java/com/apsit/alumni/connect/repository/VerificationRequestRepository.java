package com.apsit.alumni.connect.repository;

import com.apsit.alumni.connect.model.User; // --- NEW IMPORT ---
import com.apsit.alumni.connect.model.VerificationRequest;
import com.apsit.alumni.connect.model.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VerificationRequestRepository extends JpaRepository<VerificationRequest, Long> {

    List<VerificationRequest> findByStatus(VerificationStatus status);
    
    long countByStatus(VerificationStatus status);

    // --- NEW METHOD ---
    void deleteAllByUser(User user);
}
