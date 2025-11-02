package com.apsit.alumni.connect.dto;

import lombok.Data;

@Data
public class JobDto {

    private Long id;
    private String title;
    private String company;
    private String location;
    private String type;
    private String description;
    private String requirements;
    private String salary;
    private String applicationDeadline;
    private String createdAt;

    // We'll "flatten" the poster's info for easy display
    private Long postedById;
    private String postedByName;
    private String postedByCompany; // Company of the poster
}