package com.apsit.alumni.connect.repository;

import com.apsit.alumni.connect.model.Job;
import com.apsit.alumni.connect.model.User; // --- NEW IMPORT ---
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    
    List<Job> findTop3ByOrderByCreatedAtDesc();

    // --- NEW METHOD ---
    void deleteAllByPostedBy(User user);
}
