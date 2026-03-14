package com.use_management_system.user_management.util;

import com.use_management_system.user_management.dto.UserContextDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class JwtTokenUtil {

    private final Key signingKey;
    private final String issuer;
    private final String audience;
    private final long expirationMinutes;

    public JwtTokenUtil(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.issuer}") String issuer,
            @Value("${security.jwt.audience}") String audience,
            @Value("${security.jwt.expiration-minutes}") long expirationMinutes
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.issuer = issuer;
        this.audience = audience;
        this.expirationMinutes = expirationMinutes;
    }

    public String generateToken(UserContextDto userContext) {
        Instant now = Instant.now();
        Instant expiry = now.plus(expirationMinutes, ChronoUnit.MINUTES);

        return Jwts.builder()
                .setSubject(userContext.getUserId().toString())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .setIssuer(issuer)
                .setAudience(audience)
                .claim("clientId", userContext.getClientId() == null ? null : userContext.getClientId().toString())
                .claim("username", userContext.getUsername())
                .claim("roles", userContext.getRoles())
                .claim("permissions", userContext.getPermissions())
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public UserContextDto parseToken(String token) {
        Jws<Claims> jws = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .requireIssuer(issuer)
                .build()
                .parseClaimsJws(token);

        Claims claims = jws.getBody();

        String subject = claims.getSubject();
        String clientId = claims.get("clientId", String.class);
        String username = claims.get("username", String.class);

        @SuppressWarnings("unchecked")
        List<String> roles = claims.get("roles", List.class);

        @SuppressWarnings("unchecked")
        List<String> permissions = claims.get("permissions", List.class);

        return new UserContextDto(
                UUID.fromString(subject),
                clientId == null ? null : UUID.fromString(clientId),
                username,
                roles,
                permissions
        );
    }
}
