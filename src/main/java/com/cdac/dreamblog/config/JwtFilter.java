package com.cdac.dreamblog.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.cdac.dreamblog.service.implementation.CustomUserDetailsService;
import com.cdac.dreamblog.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    // Autowire JwtUtil to handle JWT token operations like extraction and validation
    @Autowired
    JwtUtil jwtUtil;

    // Autowire CustomUserDetailsService to load user details from the database
    @Autowired
    CustomUserDetailsService userDetailsService;

    /**
     * This method is the core of the filter, executed for every incoming HTTP request.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        // Get the requested URI path
        String path = req.getRequestURI();

        // ---
        // Skip JWT check for authentication endpoints (e.g., login, registration)
        // Requests to these paths don't require a JWT token for access.
        // ---
        if (path.startsWith("/api/auth")) {
            System.out.println("Skipping JWT filter for path: " + path);
            // Continue the filter chain without further JWT processing
            chain.doFilter(req, res);
            return; // Exit the method as we've handled this request
        }

        // Get the "Authorization" header from the request
        String header = req.getHeader("Authorization");

        // ---
        // Check if the header exists and starts with "Bearer " (standard JWT prefix)
        // ---
        if (header != null && header.startsWith("Bearer ")) {
            // Extract the token by removing the "Bearer " prefix
            String token = header.substring(7);
            // Extract the username from the JWT token
            String username = jwtUtil.extractUsername(token);

            // ---
            // If a username is extracted and no authentication is currently set in the SecurityContext
            // (meaning the user isn't already authenticated for this request)
            // ---
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Load user details from the database using the extracted username
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // ---
                // Validate the JWT token against the loaded user details
                // This checks for token expiration, signature validity, etc.
                // ---
                if (jwtUtil.validateToken(token)) {
                    // Create an authentication token with user details, no credentials (as it's JWT-based),
                    // and the user's authorities (roles/permissions).
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    // ---
                    // Set the authentication token in the SecurityContext.
                    // This marks the user as authenticated for the current request within Spring Security.
                    // ---
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }
        // ---
        // Continue the filter chain to the next filter or the target servlet/controller.
        // This is crucial even if no JWT was found or validated, as other filters
        // or public endpoints might still need to be processed.
        // ---
        chain.doFilter(req, res);
    }
}