package backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    //테스트용 코드
    @Setter
    private Key secretKey;

    @Value("${jwt.secret}")
    private String secret;

    @PostConstruct
    public void init() {
        if (secret == null || secret.isEmpty() || secret.getBytes().length < 32) {
            // secret이 설정되지 않았거나 충분한 길이가 아닌 경우 안전한 키를 생성
            this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        } else {
            // secret이 설정된 경우 해당 값을 사용하여 키를 생성 (길이가 충분한 경우에만)
            this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        }
    }


    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey) // secretKey를 설정
                .build() // JwtParser를 빌드
                .parseClaimsJws(token) // JWT 토큰을 파싱하여 Claims를 얻음
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(String username) {
        return createToken(username);
    }

    private String createToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10시간 유효
                .signWith(secretKey) // secretKey를 사용하여 서명
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (ExpiredJwtException e) {
            return false; // 토큰이 만료된 경우 false를 반환
        }
    }
}
