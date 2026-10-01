package com.portfoliopro.security;

import com.portfoliopro.entity.Role;
import com.portfoliopro.entity.User;
import com.portfoliopro.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {
    @Mock
    private UserRepository users;

    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        userDetailsService = new CustomUserDetailsService(users);
    }

    @Test
    void loadUserMapsStoredCredentialsAndRole() {
        User user = new User();
        user.setEmail("admin@example.com");
        user.setPassword("encoded-password");
        user.setRole(Role.ADMIN);
        when(users.findByEmail("admin@example.com")).thenReturn(Optional.of(user));

        UserDetails details = userDetailsService.loadUserByUsername("admin@example.com");

        assertEquals("admin@example.com", details.getUsername());
        assertEquals("encoded-password", details.getPassword());
        assertTrue(
                details.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void missingEmailThrowsUsernameNotFound() {
        when(users.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertEquals("User not found", assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("missing@example.com")).getMessage());
    }
}
