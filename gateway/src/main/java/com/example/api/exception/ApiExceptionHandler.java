package com.example.api.exception;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.security.Principal;

import static java.util.Objects.nonNull;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private final Tracer tracer;

    private record Response(String title, String detail, int status, String instance, String user) {
        // Align with RFC 7807 Problem Details for HTTP APIs
        private Response(final HttpStatus status, final String detail, final ServletWebRequest request) {
            this(
                    status.getReasonPhrase(),
                    detail,
                    status.value(),
                    request.getRequest().getServletPath(),
                    getUsername(request)
            );
        }

        private static String getUsername(final WebRequest request) {
            final Principal userPrincipal = request.getUserPrincipal();
            return nonNull(userPrincipal) ? userPrincipal.getName() : "anonymous";
        }
    }

    private ResponseEntity<Object> buildResponse(final HttpStatus status, final String errorMessage, final ServletWebRequest request) {
        // Add trace and span IDs to the response headers for easier troubleshooting
        final Span span = tracer.currentSpan();
        final HttpHeaders headers = new HttpHeaders();
        if (nonNull(span)) {
            headers.add("X-B3-TraceId", span.context().traceId());
            headers.add("X-B3-SpanId", span.context().spanId());
        }
        return ResponseEntity
                .status(status)
                .headers(headers)
                .body(new Response(status, errorMessage, request));
    }

    @ExceptionHandler(InvalidProductException.class)
    protected ResponseEntity<Object> handle(InvalidProductException e, ServletWebRequest request) {
        log.error("Invalid product, message: {}, product: {}", e.getMessage(), e.getProduct());
        final HttpStatus status = HttpStatus.BAD_REQUEST;
        return buildResponse(status, e.getMessage(), request);
    }

    @ExceptionHandler(InvalidUserException.class)
    protected ResponseEntity<Object> handle(InvalidUserException e, ServletWebRequest request) {
        log.error("Invalid user, message: {}", e.getMessage());
        final HttpStatus status = HttpStatus.BAD_REQUEST;
        return buildResponse(status, e.getMessage(), request);
    }

    @ExceptionHandler(UserNotFoundException.class)
    protected ResponseEntity<Object> handle(UserNotFoundException e, ServletWebRequest request) {
        log.error("User not found, message: {}, username: {}", e.getMessage(), e.getUsername());
        final HttpStatus status = HttpStatus.NOT_FOUND;
        return buildResponse(status, e.getMessage(), request);
    }

    @ExceptionHandler(DownstreamServiceException.class)
    protected ResponseEntity<Object> handle(DownstreamServiceException e, ServletWebRequest request) {
        log.error("Downstream server error, error: {}", e.getMessage());
        final HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;
        return buildResponse(status, e.getMessage(), request);
    }

}
