package com.portfoliopro.security;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {
    private static final String EMAIL = "trader@example.com";
    private static final String TOKEN = "signed-token";

    @Mock
    private JwtService jwt;
    @Mock
    private CustomUserDetailsService users;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(jwt, users);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void requestWithoutBearerHeaderContinuesWithoutAuthentication() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = (servletRequest, servletResponse) -> response.setStatus(204);

        filter.doFilterInternal(request, response, chain);

        assertEquals(204, response.getStatus());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verifyNoInteractions(jwt, users);
    }

    @Test
    void validBearerTokenSetsAuthenticationAndContinues() throws Exception {
        UserDetails details = User.withUsername(EMAIL).password("encoded-password").roles("USER").build();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + TOKEN);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = (servletRequest, servletResponse) -> response.setStatus(204);
        when(jwt.extractUsername(TOKEN)).thenReturn(EMAIL);
        when(users.loadUserByUsername(EMAIL)).thenReturn(details);
        when(jwt.isValid(TOKEN, details)).thenReturn(true);

        filter.doFilterInternal(request, response, chain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(details, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        assertEquals(204, response.getStatus());
        verify(jwt).isValid(TOKEN, details);
    }

    @Test
    void invalidBearerTokenContinuesWithoutAuthentication() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + TOKEN);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = (servletRequest, servletResponse) -> response.setStatus(204);
        when(jwt.extractUsername(TOKEN)).thenThrow(new IllegalArgumentException("invalid token"));

        filter.doFilterInternal(request, response, chain);

        assertEquals(204, response.getStatus());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(users, never()).loadUserByUsername(EMAIL);
    }

    @Test
    void existingAuthenticationIsNotReplaced() throws Exception {
        var existing = new UsernamePasswordAuthenticationToken("existing-user", null, java.util.List.of());
        SecurityContextHolder.getContext().setAuthentication(existing);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + TOKEN);
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(jwt.extractUsername(TOKEN)).thenReturn(EMAIL);

        filter.doFilterInternal(request, response, (servletRequest, servletResponse) -> {
        });

        assertEquals(existing, SecurityContextHolder.getContext().getAuthentication());
        verify(users, never()).loadUserByUsername(EMAIL);
        verify(jwt, never()).isValid(TOKEN, null);
    }
}
