package backend.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtUtil.init();  // secretKey를 초기화
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

        Key testKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        jwtUtil.setSecretKey(testKey);

        // 만료된 JWT 토큰 생성
        String expiredToken = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 10)) // 10시간 전 발행
                .setExpiration(new Date(System.currentTimeMillis() - 1000 * 60 * 60)) // 1시간 전 만료
                .signWith(testKey, SignatureAlgorithm.HS256) // 동일한 키와 알고리즘으로 서명
                .compact();

        boolean isValid = jwtUtil.validateToken(expiredToken, userDetails);

        // 만료된 토큰이므로 isValid는 false이어야 함
        assertFalse(isValid);
    }

}
