package com.use_management_system.user_management.util;

import com.use_management_system.user_management.exception.VerificationTokenExpiredException;
import com.use_management_system.user_management.exception.VerificationTokenInvalidException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmailVerificationTokenUtilTest {

    private static final String SECRET = "nX7sV2fK9qLpR4mYtZ8wD3aHjU6eC1bNxT5rQvW2yP8kLmZ4s";
    private static final String ISSUER = "user-management-service";
    private static final String AUDIENCE = "internal-services";

    @Test
    void generateAndParseShouldReturnPayload() {
        EmailVerificationTokenUtil util = new EmailVerificationTokenUtil(SECRET, ISSUER, AUDIENCE, 15);

        UUID userId = UUID.randomUUID();
        String token = util.generateToken(userId, "john@example.com", 2);
        var parsed = util.parseToken(token);

        assertEquals(userId, parsed.userId());
        assertEquals("john@example.com", parsed.email());
        assertEquals(2, parsed.emailVerificationVersion());
    }

    @Test
    void parseShouldFailForExpiredToken() {
        EmailVerificationTokenUtil util = new EmailVerificationTokenUtil(SECRET, ISSUER, AUDIENCE, -1);

        String token = util.generateToken(UUID.randomUUID(), "john@example.com", 1);

        assertThrows(VerificationTokenExpiredException.class, () -> util.parseToken(token));
    }

    @Test
    void parseShouldFailForInvalidPurpose() {
        EmailVerificationTokenUtil util = new EmailVerificationTokenUtil(SECRET, ISSUER, AUDIENCE, 15);

        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        String token = Jwts.builder()
                .setSubject(UUID.randomUUID().toString())
                .setIssuer(ISSUER)
                .setAudience(AUDIENCE)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 5 * 60 * 1000))
                .claim("purpose", "access_token")
                .claim("email", "john@example.com")
                .claim("evv", 1)
                .signWith(Keys.hmacShaKeyFor(keyBytes), SignatureAlgorithm.HS256)
                .compact();

        assertThrows(VerificationTokenInvalidException.class, () -> util.parseToken(token));
    }
}
