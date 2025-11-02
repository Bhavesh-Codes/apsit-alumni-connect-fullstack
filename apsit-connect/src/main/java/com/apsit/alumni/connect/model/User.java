package com.apsit.alumni.connect.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "users")
@Data
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // --- NEW FIELD ---
    @Column(nullable = false)
    private boolean isActive = false; // Default to false
    // --- END NEW FIELD ---

    // --- PROFILE FIELDS ---
    private String branch;
    private String graduationYear;
    private String title;
    private String company;
    private String location;

    @ElementCollection(fetch = FetchType.LAZY) 
    @CollectionTable(name = "user_skills", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "skill")
    private List<String> skills;
    
    @Lob
    @Column(length = 2000) 
    private String about;
    
    private String profileImageUrl;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_experience", joinColumns = @JoinColumn(name = "user_id"))
    private List<Experience> experience;
    
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_education", joinColumns = @JoinColumn(name = "user_id"))
    private List<Education> education;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // --- UserDetails IMPLEMENTATION ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; 
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // --- THIS IS THE CHANGE ---
        // Spring Security's "isEnabled" will now check our "isActive" flag
        return this.isActive;
        // --- END OF CHANGE ---
    }
}