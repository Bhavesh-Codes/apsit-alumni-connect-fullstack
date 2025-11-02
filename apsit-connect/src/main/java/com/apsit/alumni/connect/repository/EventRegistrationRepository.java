package com.apsit.alumni.connect.repository;

import com.apsit.alumni.connect.model.Event;
import com.apsit.alumni.connect.model.EventRegistration;
import com.apsit.alumni.connect.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {

    boolean existsByEventAndUser(Event event, User user);

    void deleteByEvent(Event event);

    // --- NEW METHOD ---
    void deleteAllByUser(User user);
}
