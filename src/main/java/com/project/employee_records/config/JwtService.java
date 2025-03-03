package com.project.employee_records.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    private final SecretKey signKey;
    private final Integer tokenValidityInMin;

    public JwtService(@Value("${jwt.secret-key}") String secretKey, @Value("${jwt.token-validity-in-min}") Integer tokenValidityInMin) {
        this.signKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.tokenValidityInMin = tokenValidityInMin;
    }

    private SecretKey getSignKey() {
        return signKey;
    }

    public Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return userName.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        final Date tokenExpiration = extractExpiration(token);
        return tokenExpiration.before(Date.from(Instant.now()));
    }

    public  String generateToken(Map<String, Object> extractClaims, UserDetails userDetails, String role, Integer id) {
        final Instant now = Instant.now();
        final Instant expiration = now.plus(tokenValidityInMin, ChronoUnit.MINUTES);
        return Jwts
                .builder()
                .setClaims(extractClaims)
                .claim("role", role)
                .claim("id", id)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(UserDetails userDetails, String role, Integer id) {
        return generateToken(Map.of(), userDetails, role, id);
    }
}