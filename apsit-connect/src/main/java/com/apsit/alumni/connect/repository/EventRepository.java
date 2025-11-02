package com.apsit.alumni.connect.repository;

import com.apsit.alumni.connect.model.Event;
import com.apsit.alumni.connect.model.User; // --- NEW IMPORT ---
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findTop3ByOrderByCreatedAtDesc();

    // --- NEW METHOD ---
    void deleteAllByOrganizer(User user);
}
