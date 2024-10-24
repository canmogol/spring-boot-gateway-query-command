package com.example.app.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.server.mvc.filter.FilterSupplier;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

@Component
@RequiredArgsConstructor
public class AuthorizationFilterSupplier extends OncePerRequestFilter implements FilterSupplier {

    private static final Class<?> filtersClass = AuthorizationFilterFunction.class;

    private final TokenUtil tokenUtil;

    @Override
    public Collection<Method> get() {
        return Arrays.asList(filtersClass.getMethods());
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        request.setAttribute(TokenUtil.class.getName(), tokenUtil);
        filterChain.doFilter(request, response);
    }
}