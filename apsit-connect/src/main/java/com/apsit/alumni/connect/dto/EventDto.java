package com.apsit.alumni.connect.dto;

import lombok.Data;

@Data
public class EventDto {

    private Long id;
    private String title;
    private String date;
    private String time;
    private String location;
    private String type;
    private String description;
    private Integer maxAttendees;
    private String createdAt;

    private Long organizerId;
    private String organizerName;
    private String organizerCompany;

    // --- THIS IS THE FIX ---
    // This field was missing
    private boolean isRegisteredByCurrentUser;
    // --- END OF FIX ---
}
