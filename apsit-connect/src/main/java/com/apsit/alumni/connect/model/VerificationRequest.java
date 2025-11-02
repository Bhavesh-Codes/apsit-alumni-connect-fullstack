package com.apsit.alumni.connect.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "verification_requests")
@Data
public class VerificationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // The student who made the request

    @Column(nullable = false)
    private String documentUrl; // URL to the uploaded document

    // --- NEW FIELD ---
    @Column(unique = true) // No two requests can ask for the same new email
    private String newEmail; // The requested new email (e.g., @gmail.com)
    // --- END NEW FIELD ---

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationStatus status;

    @Column(updatable = false)
    private LocalDateTime submittedAt;

    private LocalDateTime reviewedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by_admin_id")
    private User reviewedBy; // The admin who reviewed it

    @PrePersist
    protected void onCreate() {
        this.submittedAt = LocalDateTime.now();
        this.status = VerificationStatus.PENDING;
    }
}