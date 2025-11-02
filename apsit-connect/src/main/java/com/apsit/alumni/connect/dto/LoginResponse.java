package com.apsit.alumni.connect.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor // A handy Lombok annotation to create a constructor
public class LoginResponse {
    private String jwtToken;
    private UserDto user;
}