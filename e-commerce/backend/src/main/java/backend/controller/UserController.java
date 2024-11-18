package backend.controller;

import backend.entity.User;
import backend.repository.UserRepository;
import backend.security.JwtUtil;
import backend.service.CustomUserDetailService;
import backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("api/auth")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtTUtil;
    private final CustomUserDetailService customUserDetailService;


    public UserController(UserService userService,
                          AuthenticationManager authenticationManager,
                          JwtUtil jwtTUtil,
                          CustomUserDetailService customUserDetailService,
                          UserRepository userRepository) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTUtil = jwtTUtil;
        this.customUserDetailService = customUserDetailService;
    }


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
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        try {
            // 사용자 인증 시도
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
            );

            // 인증 성공 후 JWT 생성
            final UserDetails userDetails = customUserDetailService.loadUserByUsername(user.getEmail());
            final String jwt = jwtTUtil.generateToken(userDetails.getUsername());

            // 성공적으로 로그인한 경우 JWT 반환
            return ResponseEntity.ok(jwt);
        } catch (BadCredentialsException e) {
            // 잘못된 자격 증명 (이메일 또는 비밀번호가 틀림)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        } catch (UsernameNotFoundException e) {
            // 사용자 이메일이 데이터베이스에 없음
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        } catch (Exception e) {
            // 기타 예외 처리
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

}
