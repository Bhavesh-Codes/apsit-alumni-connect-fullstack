package com.apsit.alumni.connect.config;

import com.apsit.alumni.connect.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // Make this a Spring Bean so we can use it
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 1. Check for the "Authorization" header and "Bearer " prefix
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // If no token, pass to the next filter
            return;
        }

        // 2. Extract the token (it's after "Bearer ")
        jwt = authHeader.substring(7);

        // 3. Extract the email from the token
        userEmail = jwtService.extractUsername(jwt);

        // 4. Check if email exists and user is not *already* authenticated
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // 5. Get user details from the database
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // 6. Validate the token
            if (jwtService.isTokenValid(jwt, userDetails)) {
                
                // 7. If valid, create an auth token and set it in the SecurityContext
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // We don't need credentials
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                
                // This is the line that "logs in" the user for this request
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        // 8. Pass the request to the next filter in the chain
        filterChain.doFilter(request, response);
    }
}