package com.example.app.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Authorization: Bearer <token>
        String authValue = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (nonNull(authValue) && authValue.startsWith(BEARER_PREFIX)) {
            String authToken = authValue.substring(BEARER_PREFIX.length());
            Authentication authentication = authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        // continue with the filter chain
        filterChain.doFilter(request, response);
    }

    public Authentication authenticate(final String authToken) throws AuthenticationException {
        if (!jwtUtil.validateToken(authToken)) {
            throw new CredentialsExpiredException("Invalid token");
        }
        String user = jwtUtil.getUsernameFromToken(authToken);
        List<String> roles = jwtUtil.getRoleList(authToken);
        return new UsernamePasswordAuthenticationToken(
                user, null,
                roles.stream().map(SimpleGrantedAuthority::new).toList()
        );
    }
}
