package com.apsit.alumni.connect.service;

import com.apsit.alumni.connect.dto.CreateEventRequest;
import com.apsit.alumni.connect.dto.EventDto;
import com.apsit.alumni.connect.model.Event;
import com.apsit.alumni.connect.model.EventRegistration;
import com.apsit.alumni.connect.model.Role;
import com.apsit.alumni.connect.model.User;
import com.apsit.alumni.connect.repository.EventRegistrationRepository;
import com.apsit.alumni.connect.repository.EventRepository;
import com.apsit.alumni.connect.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventRegistrationRepository registrationRepository; 

    public EventService(EventRepository eventRepository, 
                        UserRepository userRepository,
                        EventRegistrationRepository registrationRepository) { 
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.registrationRepository = registrationRepository; 
    }

    // Now public and includes the registration check
    public EventDto mapToEventDto(Event event, User currentUser) {
        EventDto dto = new EventDto();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDate(event.getDate());
        dto.setTime(event.getTime());
        dto.setLocation(event.getLocation());
        dto.setType(event.getType());
        dto.setDescription(event.getDescription());
        dto.setMaxAttendees(event.getMaxAttendees());
        dto.setCreatedAt(event.getCreatedAt().toString());

        User organizer = event.getOrganizer();
        dto.setOrganizerId(organizer.getId());
        dto.setOrganizerName(organizer.getName());
        dto.setOrganizerCompany(organizer.getCompany());

        // This check is now valid because EventDto has the field
        boolean isRegistered = registrationRepository.existsByEventAndUser(event, currentUser);
        dto.setRegisteredByCurrentUser(isRegistered);

        return dto;
    }

    // Now correctly takes userEmail
    public List<EventDto> getAllEvents(String userEmail) {
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return eventRepository.findAll()
                .stream()
                .map(event -> mapToEventDto(event, currentUser)) // Pass currentUser
                .collect(Collectors.toList());
    }

    public EventDto createEvent(CreateEventRequest request, String userEmail) {
        User organizer = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setDate(request.getDate());
        event.setTime(request.getTime());
        event.setLocation(request.getLocation());
        event.setType(request.getType());
        event.setDescription(request.getDescription());
        event.setMaxAttendees(request.getMaxAttendees());
        event.setOrganizer(organizer);
        Event savedEvent = eventRepository.save(event);
        return mapToEventDto(savedEvent, organizer);
    }

    // Includes Admin Bug Fix
    public String registerForEvent(Long eventId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getRole() == Role.ROLE_ADMIN) {
            throw new IllegalArgumentException("Admins cannot register for events.");
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        if (registrationRepository.existsByEventAndUser(event, user)) {
            throw new IllegalArgumentException("You are already registered for this event.");
        }
        
        EventRegistration registration = new EventRegistration();
        registration.setEvent(event);
        registration.setUser(user);
        registrationRepository.save(registration);
        return "Successfully registered for event: " + event.getTitle();
    }

    // Includes Delete Bug Fix
    @Transactional
    public void deleteEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with ID: " + eventId));
        
        // 1. Delete all registrations for this event FIRST
        registrationRepository.deleteByEvent(event);
        
        // 2. Now it is safe to delete the event
        eventRepository.delete(event);
    }
}
