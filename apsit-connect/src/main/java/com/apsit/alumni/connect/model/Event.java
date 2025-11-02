package com.apsit.alumni.connect.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String date; // Using String for simplicity (e.g., "2025-12-25")
    private String time; // e.g., "10:00 AM"
    private String location; // Can be "Online" or a physical address
    private String type; // e.g., "Networking", "Webinar"

    @Lob
    @Column(length = 5000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_user_id", nullable = false)
    private User organizer;

    private Integer maxAttendees; // Max number of people

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}