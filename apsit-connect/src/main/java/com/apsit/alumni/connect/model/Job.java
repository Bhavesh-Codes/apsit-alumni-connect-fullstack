package com.apsit.alumni.connect.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
@Data
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String company;

    private String location;

    // e.g., "Full-time", "Internship"
    private String type; 

    @Lob
    @Column(length = 5000) // 5000 chars for a long description
    private String description;

    @Lob
    @Column(length = 5000)
    private String requirements;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "posted_by_user_id", nullable = false)
    private User postedBy;

    private String salary; // e.g., "Competitive", "$100,000", "Not Disclosed"

    private String applicationDeadline; // Using String for simplicity

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}