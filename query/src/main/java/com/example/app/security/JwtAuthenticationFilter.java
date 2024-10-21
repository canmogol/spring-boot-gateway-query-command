package com.example.app.security;

import com.example.app.exception.JwtParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final int JWT_PARTS_COUNT = 3;

    private final JsonMapper jsonMapper;

    @Value("${jwt.claims.roles}")
    private String rolesFieldName;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Authorization: Bearer <token>
        String authValue = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (nonNull(authValue) && authValue.startsWith(BEARER_PREFIX)) {
            String authToken = authValue.substring(BEARER_PREFIX.length());
            Token token = readToken(authToken);
            request.setAttribute(Token.class.getName(), token);
            request.setAttribute(Token.TOKEN_SUBJECT, token.subject());
            request.setAttribute(Token.TOKEN_ROLES, token.roles());
        }
        // continue with the filter chain
        filterChain.doFilter(request, response);
    }

    private Token readToken(String authToken) {
        String[] parts = authToken.split("\\.");
        if (parts.length != JWT_PARTS_COUNT) {
            throw new JwtParseException("Invalid token, expected 3 parts");
        }
        String payload = parts[1];
        try {
            byte[] decoded = Base64.getDecoder().decode(payload);
            JsonNode jsonNode = jsonMapper.readTree(decoded);
            String subject = jsonNode.get("sub").asText();
            List<String> roles = jsonNode.get(rolesFieldName).findValuesAsText(rolesFieldName);
            return new Token(subject, roles);
        } catch (IllegalArgumentException | IOException e) {
            throw new JwtParseException("Invalid token", e);
        }
    }

}
