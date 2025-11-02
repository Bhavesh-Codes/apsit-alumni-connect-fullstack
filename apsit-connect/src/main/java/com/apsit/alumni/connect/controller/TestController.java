package com.apsit.alumni.connect.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test") // <-- NOTE THIS NEW URL
public class TestController {

    @GetMapping("/hello")
    public ResponseEntity<String> getSecuredHello() {
        return ResponseEntity.ok("Hello! This is a fully SECURED endpoint.");
    }
}