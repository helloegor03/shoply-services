package com.helloegor03.product_service.config;

import com.helloegor03.product_service.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthTokenFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthTokenFilter(JwtUtil jwtUtils, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String header = request.getHeader("Authorization");
            System.out.println("Authorization header: " + header);

            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                System.out.println("Token: " + token);

                if (jwtUtils.validateJwtToken(token)) {
                    String username = jwtUtils.getUsernameFromToken(token);
                    System.out.println("Username from token: " + username);

                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        var userDetails = userDetailsService.loadUserByUsername(username);
                        System.out.println("Loaded user: " + userDetails.getUsername());

                        var auth = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );
                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                        System.out.println("Authentication set in SecurityContext");
                    }
                } else {
                    System.out.println("Token is invalid");
                }
            } else {
                System.out.println("Authorization header missing or does not start with Bearer");
            }
        } catch (Exception e) {
            System.out.println("Cannot set user authentication: " + e.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}
