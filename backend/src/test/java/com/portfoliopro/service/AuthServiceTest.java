package com.portfoliopro.service;

import com.portfoliopro.dto.AuthResponse;
import com.portfoliopro.dto.LoginRequest;
import com.portfoliopro.dto.RegisterRequest;
import com.portfoliopro.entity.Portfolio;
import com.portfoliopro.entity.Role;
import com.portfoliopro.entity.User;
import com.portfoliopro.repository.PortfolioRepository;
import com.portfoliopro.repository.UserRepository;
import com.portfoliopro.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository users;
    @Mock
    private PortfolioRepository portfolios;
    @Mock
    private PasswordEncoder encoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwt;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(users, portfolios, encoder, authenticationManager, jwt);
    }

    @Test
    void registerNormalizesIdentityCreatesPortfolioAndReturnsToken() {
        when(users.findByEmail("jane@example.com")).thenReturn(Optional.empty());
        when(encoder.encode("password-123")).thenReturn("encoded-password");
        when(users.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(7L);
            return saved;
        });
        when(jwt.generate(any(UserDetails.class))).thenReturn("signed-token");

        AuthResponse response = authService.register(
                new RegisterRequest(" Jane Doe ", " JANE@EXAMPLE.COM ", "password-123"));

        assertEquals("signed-token", response.token());
        assertEquals(7L, response.userId());
        assertEquals("Jane Doe", response.name());
        assertEquals("jane@example.com", response.email());
        assertEquals("USER", response.role());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(users).save(userCaptor.capture());
        assertEquals("encoded-password", userCaptor.getValue().getPassword());
        assertEquals(Role.USER, userCaptor.getValue().getRole());

        ArgumentCaptor<Portfolio> portfolioCaptor = ArgumentCaptor.forClass(Portfolio.class);
        verify(portfolios).save(portfolioCaptor.capture());
        assertEquals(userCaptor.getValue(), portfolioCaptor.getValue().getUser());
        verify(jwt).generate(any(UserDetails.class));
    }

    @Test
    void registerRejectsAnAlreadyRegisteredEmail() {
        when(users.findByEmail("jane@example.com")).thenReturn(Optional.of(new User()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.register(new RegisterRequest("Jane", "JANE@example.com", "password-123")));

        assertEquals("Email is already registered", exception.getMessage());
        verify(users, never()).save(any(User.class));
        verify(portfolios, never()).save(any(Portfolio.class));
    }

    @Test
    void loginNormalizesEmailAuthenticatesAndReturnsToken() {
        User user = new User();
        user.setId(9L);
        user.setName("Jane Doe");
        user.setEmail("jane@example.com");
        user.setPassword("encoded-password");
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(null);
        when(users.findByEmail("jane@example.com")).thenReturn(Optional.of(user));
        when(jwt.generate(any(UserDetails.class))).thenReturn("login-token");

        AuthResponse response = authService.login(new LoginRequest(" JANE@EXAMPLE.COM ", "password-123"));

        assertEquals("login-token", response.token());
        assertEquals(9L, response.userId());
        assertEquals("jane@example.com", response.email());
        assertEquals("USER", response.role());
        verify(authenticationManager).authenticate(any(Authentication.class));
        verify(jwt).generate(any(UserDetails.class));
    }

    @Test
    void loginPropagatesAuthenticationFailureWithoutLoadingUser() {
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class,
                () -> authService.login(new LoginRequest("jane@example.com", "wrong-password")));

        verify(users, never()).findByEmail("jane@example.com");
    }
}
