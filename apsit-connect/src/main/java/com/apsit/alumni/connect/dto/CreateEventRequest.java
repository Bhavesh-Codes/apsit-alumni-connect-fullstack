package com.apsit.alumni.connect.dto;

import lombok.Data;

@Data
public class CreateEventRequest {
    // This DTO contains all the fields from the "Create Event" form
    private String title;
    private String date;
    private String time;
    private String location;
    private String type;
    private String description;
    private Integer maxAttendees;
}