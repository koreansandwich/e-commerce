package backend.controller;

import backend.entity.User;
import backend.repository.UserRepository;
import backend.security.JwtUtil;
import backend.service.CustomUserDetailService;
import backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtTUtil;

    @Autowired
    private CustomUserDetailService customUserDetailService;

    @Autowired
    private UserRepository userRepository;


    @PostMapping("/register")
    public String registerUser(@RequestBody User user) {
        try {
            userService.registerUser(user);
            return "User registered successfully.";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    @PostMapping("/login")
    public String loginUser(@RequestBody User user) {
        try {

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

            final UserDetails userDetails = customUserDetailService.loadUserByUsername(user.getEmail());

            final String jwt = jwtTUtil.generateToken(userDetails.getUsername());

            return jwt;
        } catch (Exception e) {
            e.printStackTrace();  // 콘솔에 전체 스택 트레이스를 출력
            return "An unexpected error occurred: " + e.getMessage();  // 예외 메시지를
        }
    }
}
