package com.example.app.config;

import com.example.app.security.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final Tracer tracer;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JsonMapper jsonMapper;

    @Value("${gateway.public.endpoints}")
    private String[] publicEndpoints;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                        requests -> {
                            // permit all public endpoints
                            for (String endpoint : publicEndpoints) {
                                requests = requests.requestMatchers(endpoint).permitAll();
                            }
                            // permit all OPTIONS requests
                            requests.requestMatchers(HttpMethod.OPTIONS).permitAll()
                                    // require authentication for all other requests
                                    .anyRequest().authenticated();
                        }
                )
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(LogoutConfigurer::permitAll)
                .exceptionHandling(configurer -> {
                    // Security related exceptions are handled here
                    configurer.accessDeniedHandler(this::writeExceptionResponse);
                    configurer.authenticationEntryPoint(this::writeExceptionResponse);
                });
        return http.build();
    }

    private void writeExceptionResponse(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Exception exception
    ) throws IOException {
        // Add trace and span IDs to the response headers for easier troubleshooting
        final Span span = tracer.currentSpan();
        if (nonNull(span)) {
            response.setHeader("X-B3-TraceId", span.context().traceId());
            response.setHeader("X-B3-SpanId", span.context().spanId());
        }
        response.setStatus(isNull(request.getUserPrincipal()) ? HttpServletResponse.SC_UNAUTHORIZED : HttpServletResponse.SC_FORBIDDEN);
        // Align with RFC 7807 Problem Details for HTTP APIs
        final Map<String, Object> keyValues = Map.of(
                "title", isNull(request.getUserPrincipal()) ? "Unauthorized" : "Forbidden",
                "detail", exception.getMessage(),
                "status", response.getStatus(),
                "instance", request.getRequestURI(),
                "user", nonNull(request.getUserPrincipal()) ? request.getUserPrincipal().getName() : "anonymous"
        );
        // Set the content type to JSON and write the response as JSON
        response.setContentType("application/json");
        final String message = jsonMapper.writeValueAsString(keyValues);
        response.getWriter().write(message);
    }

}
