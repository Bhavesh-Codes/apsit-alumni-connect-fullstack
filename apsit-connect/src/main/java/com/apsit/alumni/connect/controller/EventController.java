package com.apsit.alumni.connect.controller;

import com.apsit.alumni.connect.dto.CreateEventRequest;
import com.apsit.alumni.connect.dto.EventDto;
import com.apsit.alumni.connect.model.User;
import com.apsit.alumni.connect.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // --- THIS IS THE FIX ---
    @GetMapping
    public ResponseEntity<List<EventDto>> getAllEvents(
            @AuthenticationPrincipal User userDetails // 1. Get the logged-in user
    ) {
        // 2. Pass the user's email to the service
        List<EventDto> events = eventService.getAllEvents(userDetails.getEmail()); 
        return ResponseEntity.ok(events);
    }
    // --- END OF FIX ---

    @PostMapping
    @PreAuthorize("hasRole('ALUMNI')") 
    public ResponseEntity<EventDto> createEvent(
            @RequestBody CreateEventRequest request,
            @AuthenticationPrincipal User userDetails
    ) {
        EventDto newEvent = eventService.createEvent(request, userDetails.getEmail());
        return new ResponseEntity<>(newEvent, HttpStatus.CREATED);
    }

    @PostMapping("/{eventId}/register")
    public ResponseEntity<String> registerForEvent(
            @PathVariable Long eventId,
            @AuthenticationPrincipal User userDetails
    ) {
        String responseMessage = eventService.registerForEvent(eventId, userDetails.getEmail());
        return ResponseEntity.ok(responseMessage);
    }

    @DeleteMapping("/{eventId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteEvent(
            @PathVariable Long eventId
    ) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.ok(Map.of("message", "Event deleted successfully."));
    }
}
