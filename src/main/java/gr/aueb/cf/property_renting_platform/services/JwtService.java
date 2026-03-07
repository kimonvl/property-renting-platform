package gr.aueb.cf.property_renting_platform.services;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final SecretKey key;
    private final long accessMinutes;

    public JwtService(
            @Value("${app.jwt.secret-base64}") String secretBase64,
            @Value("${app.jwt.access-minutes}") long accessMinutes
    ) {
        byte[] decoded = Base64.getDecoder().decode(secretBase64);
        this.key = Keys.hmacShaKeyFor(decoded);
        this.accessMinutes = accessMinutes;
    }

    public String generateAccessToken(long userId, String email, String role) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(accessMinutes * 60);

        return Jwts.builder()
                .subject(email)
                .claims(Map.of(
                        "uid", userId,
                        "role", role
                ))
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public Jws<Claims> parseAndValidate(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
    }
}
