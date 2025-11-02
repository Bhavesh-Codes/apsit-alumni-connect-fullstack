package com.apsit.alumni.connect.dto;

import lombok.Data;

@Data
public class CreateJobRequest {
    // This DTO contains all the fields from the "Post Job" form
    private String title;
    private String company;
    private String location;
    private String type;
    private String description;
    private String requirements;
    private String salary;
    private String applicationDeadline;
}