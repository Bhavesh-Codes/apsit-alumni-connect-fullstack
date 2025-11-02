package com.apsit.alumni.connect.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable // This tells JPA this object is part of another entity
@Data
public class Experience {

    private String title;
    private String company;
    private String startDate; // Using String for simplicity
    private String endDate;   // Can be "Present"

    @Column(length = 2000) // Allow for long descriptions
    private String description;

    private boolean currentJob;
}
