package com.bk.ems3.security;

import com.bk.ems3.model.User;
import com.bk.ems3.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        System.out.println("🔍 JwtFilter - Authorization Header: " + authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            System.out.println("🔍 Token extracted: " + token.substring(0, Math.min(20, token.length())) + "...");

            if (jwtUtil.validateToken(token)) {
                String email = jwtUtil.extractEmail(token);
                System.out.println("✓ Token valid. Email: " + email);

                User user = userRepository.findByEmail(email).orElse(null);

                if (user != null) {
                    SimpleGrantedAuthority authority =
                            new SimpleGrantedAuthority("ROLE_"+user.getRole().name());
                    System.out.println("✓ User found with role: " + authority);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    user,
                                    null,
                                    List.of(new SimpleGrantedAuthority("ROLE_"+user.getRole().name()))
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("✓ Authentication set");
                } else {
                    System.out.println("✗ User not found in database");
                }
            } else {
                System.out.println("✗ Token validation failed");
            }
        } else {
            System.out.println("✗ No Bearer token found");
        }

        filterChain.doFilter(request, response);
    }
}