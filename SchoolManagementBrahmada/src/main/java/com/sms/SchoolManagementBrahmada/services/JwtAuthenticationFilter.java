
package com.sms.SchoolManagementBrahmada.services;

import java.io.IOException;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AppUserService appUserService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String bearerToken = request.getHeader("Authorization");
            if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
                throw new Exception("Authorization Bearer not found");
            }

            String jwt = bearerToken.substring(7);
            Claims claims = jwtService.getTokenClaims(jwt);
            if (claims == null) {
                throw new Exception("Token not Valid");
            }

            String email = claims.getSubject();
            String role = (String) claims.get("role");

            if (email != null && role != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var userDetails = appUserService.loadUserByUsername(email);

              
                var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (Exception ex) {
            System.out.println("Can't authenticate user: " + ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}

