package backend.controller;
import backend.entity.User;
import backend.service.CustomUserDetailService;
import backend.service.UserService;
import backend.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private CustomUserDetailService customUserDetailService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void test_registerUser_success() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password123");

        doNothing().when(userService).registerUser(any(User.class));

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully."));

        verify(userService, times(1)).registerUser(any(User.class));
    }

    @Test
    void test_loginUser_success() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).
                thenReturn(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

        when(customUserDetailService.loadUserByUsername(user.getEmail()))
                .thenReturn(new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>()));

        when(jwtUtil.generateToken(any())).thenReturn("token");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("token"));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(customUserDetailService, times(1)).loadUserByUsername(user.getEmail());
        verify(jwtUtil, times(1)).generateToken(any());

    }
}