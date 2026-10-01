package com.portfoliopro.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {
    private static final String SECRET = "PortfolioPro test secret with more than thirty-two bytes";

    private final UserDetails trader = User.withUsername("trader@example.com")
            .password("encoded-password")
            .roles("USER")
            .build();

    @Test
    void generateAndExtractUsername() {
        JwtService jwtService = new JwtService(SECRET, 60_000L);

        String token = jwtService.generate(trader);

        assertEquals("trader@example.com", jwtService.extractUsername(token));
    }

    @Test
    void validTokenMatchesItsUser() {
        JwtService jwtService = new JwtService(SECRET, 60_000L);
        String token = jwtService.generate(trader);

        assertTrue(jwtService.isValid(token, trader));
    }

    @Test
    void tokenForDifferentUserIsInvalid() {
        JwtService jwtService = new JwtService(SECRET, 60_000L);
        String token = jwtService.generate(trader);
        UserDetails anotherUser = User.withUsername("other@example.com")
                .password("encoded-password")
                .roles("USER")
                .build();

        assertFalse(jwtService.isValid(token, anotherUser));
    }

    @Test
    void expiredAndWrongSignatureTokensAreInvalid() {
        JwtService expiredService = new JwtService(SECRET, -1L);
        String expiredToken = expiredService.generate(trader);
        JwtService verifier = new JwtService(SECRET, 60_000L);

        JwtService otherKeyService = new JwtService(
                "Another secret that also exceeds thirty-two bytes", 60_000L);
        String wrongSignatureToken = otherKeyService.generate(trader);

        assertFalse(verifier.isValid(expiredToken, trader));
        assertFalse(verifier.isValid(wrongSignatureToken, trader));
        assertFalse(verifier.isValid("not-a-jwt", trader));
    }
}
