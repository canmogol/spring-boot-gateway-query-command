package com.example.app.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.CredentialsExpiredException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtAuthenticationFilterTest {

    private JwtTokenUtil jwtTokenUtil = mock(JwtTokenUtil.class);

    @Test
    void should_throwException_when_tokenIsNotValid() {
        // given
        final String jwt = "token";
        final String expectedErrorMessage = "Invalid token";
        final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtTokenUtil);

        // when
        when(jwtTokenUtil.validateToken(jwt)).thenReturn(false);

        final CredentialsExpiredException thrown = assertThrows(CredentialsExpiredException.class, () -> filter.authenticate(jwt));

        // then
        assertEquals(expectedErrorMessage, thrown.getMessage());
    }

}