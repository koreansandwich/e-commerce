package backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtUtil.setSecretKey("test_secret_key");
    }

    @Test
    void test_generateToken() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        assertNotNull(token);
    }

    @Test
    void test_extractUsername() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        String extractedUsername = jwtUtil.extractUsername(token);

        assertEquals(username, extractedUsername);
    }

    @Test
    void test_extractExpiration() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        Date expirationDate = jwtUtil.extractExpiration(token);

        assertNotNull(expirationDate);
        assertTrue(expirationDate.after(new Date()));
    }


    @Test
    void test_validateToken() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        UserDetails userDetails = new User(username, "password123", new ArrayList<>());

        boolean isValid = jwtUtil.validateToken(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    void test_isTokenExpired() {
        String username = "testuser";
        UserDetails userDetails = new User(username, "password123", new ArrayList<>());

        String expiredToken = jwtUtil.generateToken(username);

        boolean isValid = jwtUtil.validateToken(expiredToken, userDetails);

        assertFalse(isValid);
    }
}