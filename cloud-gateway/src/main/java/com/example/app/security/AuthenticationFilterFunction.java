package com.example.app.security;

import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.Optional;
import java.util.function.Function;

import static java.util.Objects.isNull;

public interface AuthenticationFilterFunction {

    String BEARER = "Bearer ";

    static HandlerFilterFunction<ServerResponse, ServerResponse> authentication() {
        return (request, next) -> next.handle(authenticationFunc().apply(request));
    }

    static Function<ServerRequest, ServerRequest> authenticationFunc() {
        return request -> {
            final Optional<Object> tokenUtilOptional = request.attribute(TokenUtil.class.getName());
            if (tokenUtilOptional.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "tokenUtil not found");
            }
            final TokenUtil tokenUtil = (TokenUtil) tokenUtilOptional.get();
            final String authorization = request.headers().firstHeader("Authorization");
            if (isNull(authorization)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization header not found");
            }
            if (!authorization.startsWith(BEARER)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authorization header");
            }
            final String token = authorization.substring(BEARER.length());
            final Claims claims = tokenUtil.getClaims(token);
            if (!tokenUtil.validate(claims)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
            }
            return ServerRequest.from(request)
                    .attribute(Claims.class.getName(), claims)
                    .build();
        };
    }

}
