package com.apsit.alumni.connect.dto;

import lombok.Data;

@Data
public class NotificationDto {

    private Long id;
    private String type;
    private String title;
    private String message;
    private boolean readStatus;
    private Long relatedEntityId;
    private String createdAt;
}