package com.example.api.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.security.Principal;

import static java.util.Objects.nonNull;

@ControllerAdvice
@Slf4j
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

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

        private static String getUsername(final ServletWebRequest request) {
            final Principal userPrincipal = request.getUserPrincipal();
            return nonNull(userPrincipal) ? userPrincipal.getName() : "anonymous";
        }
    }

    @ExceptionHandler(InvalidProductException.class)
    protected ResponseEntity<Object> handle(InvalidProductException e, ServletWebRequest request) {
        log.error("Invalid product, message: {}, product: {}", e.getMessage(), e.getProduct());
        final HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(badRequest).body(new Response(badRequest, e.getMessage(), request));
    }

    @ExceptionHandler(InvalidUserException.class)
    protected ResponseEntity<Object> handle(InvalidUserException e, ServletWebRequest request) {
        log.error("Invalid user, message: {}", e.getMessage());
        final HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(badRequest).body(new Response(badRequest, e.getMessage(), request));
    }

    @ExceptionHandler(UserNotFoundException.class)
    protected ResponseEntity<Object> handle(UserNotFoundException e, ServletWebRequest request) {
        log.error("User not found, message: {}, username: {}", e.getMessage(), e.getUsername());
        final HttpStatus notFound = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(notFound).body(new Response(notFound, e.getMessage(), request));
    }

}
