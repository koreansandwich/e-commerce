package backend.service;

import backend.entity.User;
import backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void test_registerUser_success() {
        User user = new User();
        user.setName("TestUser");
        user.setEmail("test@example.com");
        user.setPassword("password123");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(null);

        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");

        userService.registerUser(user);

        verify(userRepository, times(1)).save(any(User.class));

        assertEquals("TestUser", user.getName());
        assertEquals("encodedPassword", user.getPassword());
        assertEquals("test@example.com", user.getEmail());
    }

    @Test
    void test_registerUser_failure() {
        User user = new User();
        user.setName("TestUser");
        user.setEmail("test@example.com");
        user.setPassword("password123");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(user);
        });

        assertEquals("Email already registered", exception.getMessage());
    }

    @Test
    void test_authenticateUser_success() {
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword";
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword(encodedPassword);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        boolean isAuthenticated = userService.authenticateUser(user.getEmail(), rawPassword);

        assertTrue(isAuthenticated);
    }

    @Test
    void test_authenticateUser_failure() {
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword";
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword(encodedPassword);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        boolean isAuthenticated = userService.authenticateUser(user.getEmail(), rawPassword);

        assertFalse(isAuthenticated);
    }

    @Test
    void test_authenticateUser_userNotFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(null);

        boolean isAuthenticated = userService.authenticateUser("test@example.com", "password123");

        assertFalse(isAuthenticated);
    }
}
