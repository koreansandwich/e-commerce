package backend.service;

import backend.entity.User;
import backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailService customUserDetailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void test_loadUserByUsername_success() {

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password123");

        when(userRepository.findByEmail("test@example.com")).thenReturn(user);

        assertDoesNotThrow(() -> {
            UserDetails userDetails = customUserDetailService.loadUserByUsername("test@example.com");
            assertEquals("test@example.com", userDetails.getUsername());
            assertEquals("password123", userDetails.getPassword());
        });

        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void test_loadUserByUsername_failure() {

        when(userRepository.findByEmail("test@example.com")).thenReturn(null);

        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailService.loadUserByUsername("test@example.com");
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }
}