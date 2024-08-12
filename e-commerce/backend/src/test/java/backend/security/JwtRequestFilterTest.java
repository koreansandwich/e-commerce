package backend.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.IOException;
import java.security.SignatureException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class JwtRequestFilterTest {

    @InjectMocks
    private JwtRequestFilter jwtRequestFilter;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain chain;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();

        SecurityContextHolder.clearContext();
    }

    @Test
    void testValidToken() throws Exception {
        String token = "valid.jwt.token";
        String username = "valid.jwt.username";

        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(jwtUtil.validateToken(eq(token), any(UserDetails.class))).thenReturn(true);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(new User(username, "password", new ArrayList<>()));

        request.addHeader("Authorization", "Bearer " + token);
        jwtRequestFilter.doFilterInternal(request, response, chain);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testInvalidToken() throws ServletException, IOException {
        String token = "invalid.jwt.token";

        when(jwtUtil.extractUsername(token)).thenThrow(io.jsonwebtoken.SignatureException.class);

        request.addHeader("Authorization", "Bearer" + token);

        jwtRequestFilter.doFilterInternal(request, response, chain);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testExpiredToken() throws ServletException, IOException {
        String token = "expired.jwt.token";

        when(jwtUtil.extractUsername(token)).thenThrow(ExpiredJwtException.class);
        request.addHeader("Authorization", "Bearer " + token);
        jwtRequestFilter.doFilterInternal(request, response, chain);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testNoAuthroizationHeader() throws ServletException, IOException {

        jwtRequestFilter.doFilterInternal(request, response, chain);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testInvalidAuthroizationHeader() throws ServletException, IOException {
        request.addHeader("Authorization", "InvalidHeaderFormat");
        jwtRequestFilter.doFilterInternal(request, response, chain);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}