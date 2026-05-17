package com.fooddelivery.security;

import com.fooddelivery.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final AuthService authService;

    // ✅ Constructor
    public JwtFilter(JwtUtil jwtUtil, AuthService authService) {
        this.jwtUtil = jwtUtil;
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain)
            throws ServletException, IOException {

        // ✅ 1. Skip authentication endpoints (VERY IMPORTANT FIX)
        String path = request.getServletPath();
        if (path.startsWith("/api/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ 2. Get Authorization header
        String authHeader = request.getHeader("Authorization");

        // ✅ 3. If no token → continue (do NOT block)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ 4. Extract token
        String token = authHeader.substring(7);

        try {
            // ✅ 5. Validate token
            if (jwtUtil.isTokenValid(token)) {

                String email = jwtUtil.extractEmail(token);

                // ✅ 6. Load user
                UserDetails userDetails = authService.loadUserByUsername(email);

                // ✅ 7. Create authentication
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // ✅ 8. Set security context
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

        } catch (Exception e) {
            // ❗ Do NOT block request on error → just continue
            System.out.println("JWT Error: " + e.getMessage());
        }

        // ✅ 9. Continue filter chain
        filterChain.doFilter(request, response);
    }
}