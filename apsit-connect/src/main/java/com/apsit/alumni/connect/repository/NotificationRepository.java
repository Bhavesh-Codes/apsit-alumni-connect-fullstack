package com.apsit.alumni.connect.repository;

import com.apsit.alumni.connect.model.Notification;
import com.apsit.alumni.connect.model.NotificationType;
import com.apsit.alumni.connect.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserOrderByCreatedAtDesc(User user);
    
    List<Notification> findByUserAndReadStatus(User user, boolean readStatus);
    
    void deleteByRelatedEntityIdAndType(Long relatedEntityId, NotificationType type);

    // --- NEW METHOD ---
    void deleteAllByUser(User user);
}
