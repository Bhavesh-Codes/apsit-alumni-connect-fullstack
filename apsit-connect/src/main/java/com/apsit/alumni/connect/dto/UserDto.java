package com.apsit.alumni.connect.dto;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String role;
    
    // Note: No 'password' field here!
}