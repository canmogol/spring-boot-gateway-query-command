package com.example.app.security;

import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.server.mvc.common.Shortcut;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Objects.isNull;

public interface AuthorizationFilterFunction {

    String BEARER = "Bearer ";

    @Shortcut
    static HandlerFilterFunction<ServerResponse, ServerResponse> authorization(final List<String> roles) {
        return (request, next) -> next.handle(authorizationFunc(roles).apply(request));
    }

    static Function<ServerRequest, ServerRequest> authorizationFunc(final List<String> roles) {
        return request -> {
            final Optional<Object> tokenUtilOptional = request.attribute(TokenUtil.class.getName());
            if (tokenUtilOptional.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "tokenUtil not found");
            }
            final TokenUtil tokenUtil = (TokenUtil) tokenUtilOptional.get();
            List<String> tokenRoles = getRoles(request, tokenUtil);
            if (tokenRoles.stream().noneMatch(roles::contains)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden, invalid role");
            }
            return request;
        };
    }

    private static List<String> getRoles(final ServerRequest request, final TokenUtil tokenUtil) {
        final String authorization = request.headers().firstHeader("Authorization");
        if (isNull(authorization)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization header not found");
        }
        if (!authorization.startsWith(BEARER)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authorization header");
        }
        final String token = authorization.substring(BEARER.length());

        Claims claims = getClaims(request, tokenUtil, token);
        if (!tokenUtil.validate(claims)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
        return tokenUtil.getRoles(claims);
    }

    private static Claims getClaims(final ServerRequest request, final TokenUtil tokenUtil, final String token) {
        final Optional<Object> claimsOptional = request.attribute(Claims.class.getName());
        if (claimsOptional.isEmpty()) {
            return tokenUtil.getClaims(token);
        }
        final Object claims = claimsOptional.get();
        if (!(claims instanceof Claims)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid claims");
        }
        return (Claims) claims;
    }

}
