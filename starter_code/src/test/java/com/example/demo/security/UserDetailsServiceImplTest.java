package com.example.demo.security;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserDetailsServiceImplTest {

    private UserDetailsServiceImpl userDetailsService;
    private UserRepository userRepository = mock(UserRepository.class);

    @BeforeEach
    public void setup() {
        userDetailsService = new UserDetailsServiceImpl();
        TestUtils.injectObjects(userDetailsService, "userRepository", userRepository);
    }

    @Test
    public void load_user_by_username() {
        User user = new User();
        user.setUsername("akoele");
        user.setPassword("hashedPassword");

        when(userRepository.findByUsername("akoele")).thenReturn(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername("akoele");
        assertNotNull(userDetails);
        assertEquals("akoele", userDetails.getUsername());
        assertEquals("hashedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().isEmpty());
    }

    @Test
    public void load_user_by_username_not_found() {
        when(userRepository.findByUsername("inconnu")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("inconnu");
        });
    }
}