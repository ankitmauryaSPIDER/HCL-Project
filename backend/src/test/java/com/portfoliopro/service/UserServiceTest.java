package com.portfoliopro.service;

import com.portfoliopro.entity.User;
import com.portfoliopro.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository users;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(users);
    }

    @Test
    void allReturnsRepositoryUsers() {
        List<User> expected = List.of(new User());
        when(users.findAll()).thenReturn(expected);

        assertSame(expected, userService.all());
    }

    @Test
    void getReturnsMatchingUser() {
        User expected = new User();
        when(users.findById(1L)).thenReturn(Optional.of(expected));

        assertSame(expected, userService.get(1L));
    }

    @Test
    void getThrowsWhenUserDoesNotExist() {
        when(users.findById(99L)).thenReturn(Optional.empty());

        assertEquals("User not found", assertThrows(IllegalArgumentException.class,
                () -> userService.get(99L)).getMessage());
        verify(users).findById(99L);
    }
}
