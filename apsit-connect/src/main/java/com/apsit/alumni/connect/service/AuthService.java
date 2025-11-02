package com.apsit.alumni.connect.service;

import com.apsit.alumni.connect.dto.LoginRequest;
import com.apsit.alumni.connect.dto.LoginResponse;
import com.apsit.alumni.connect.dto.RegisterRequest;
import com.apsit.alumni.connect.dto.UserDto;
import com.apsit.alumni.connect.exception.EmailAlreadyExistsException;
import com.apsit.alumni.connect.model.Role;
import com.apsit.alumni.connect.model.User;
import com.apsit.alumni.connect.model.VerificationRequest;
import com.apsit.alumni.connect.repository.UserRepository;
import com.apsit.alumni.connect.repository.VerificationRequestRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException; // --- NEW IMPORT ---
import org.springframework.security.authentication.DisabledException; // --- NEW IMPORT ---
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final VerificationRequestRepository verificationRepository;

    public AuthService(UserRepository userRepository,
                         PasswordEncoder passwordEncoder,
                         AuthenticationManager authenticationManager,
                         JwtService jwtService,
                         VerificationRequestRepository verificationRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.verificationRepository = verificationRepository;
    }

    @Transactional
    public UserDto registerUser(RegisterRequest request) {
        // ... (this method is unchanged)
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email is already in use: " + request.getEmail());
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        if (request.getEmail().endsWith("@apsit.edu.in")) {
            user.setRole(Role.ROLE_STUDENT);
            user.setActive(true);
        } else {
            user.setRole(Role.ROLE_ALUMNI);
            user.setActive(false); 
            if (request.getDocumentUrl() == null || request.getDocumentUrl().isEmpty()) {
                throw new IllegalArgumentException("Document URL is required for alumni registration.");
            }
        }

        User savedUser = userRepository.save(user);

        if (user.getRole() == Role.ROLE_ALUMNI) {
            VerificationRequest newRequest = new VerificationRequest();
            newRequest.setUser(savedUser);
            newRequest.setDocumentUrl(request.getDocumentUrl());
            verificationRepository.save(newRequest);
        }

        UserDto userDto = new UserDto();
        userDto.setId(savedUser.getId());
        userDto.setName(savedUser.getName());
        userDto.setEmail(savedUser.getEmail());
        userDto.setRole(savedUser.getRole().toString());

        return userDto;
    }

    // --- THIS IS THE UPDATED LOGIN METHOD ---
    public LoginResponse loginUser(LoginRequest request) {
        
        // 1. Find the user by email first
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password.")); // Use generic error

        // 2. Check if the user is active *before* checking the password
        if (!user.isActive()) {
            // Manually throw the exception our handler is looking for
            throw new DisabledException("Your account is not active. If you are an alumnus, please wait for admin approval.");
        }

        // 3. User is active, so now we check their password
        // This will throw BadCredentialsException if the password is wrong
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );
        
        // 4. All checks passed, generate token
        String jwtToken = jwtService.generateToken(user);

        // 5. Create the UserDto
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setRole(user.getRole().toString());

        // 6. Return the full LoginResponse
        return new LoginResponse(jwtToken, userDto);
    }
}