package backend.security;

import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import io.jsonwebtoken.SignatureAlgorithm;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        jwtUtil.setSecretKey(Base64.getEncoder().encodeToString(key.getEncoded()));
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

        // jwtUtil 인스턴스에서 사용된 동일한 키를 사용하여 토큰을 생성
        String expiredToken = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 10)) // 10시간 전
                .setExpiration(new Date(System.currentTimeMillis() - 1000 * 60 * 60)) // 1시간 전 만료
                .signWith(SignatureAlgorithm.HS256, Base64.getDecoder().decode(jwtUtil.getSecretKey())) // 동일한 키로 서명
                .compact();

        boolean isValid = jwtUtil.validateToken(expiredToken, userDetails);

        // 만료된 토큰이므로 isValid는 false이어야 함
        assertFalse(isValid);
    }
}