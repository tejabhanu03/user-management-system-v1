package com.use_management_system.user_management.util;

import com.use_management_system.user_management.exception.VerificationTokenExpiredException;
import com.use_management_system.user_management.exception.VerificationTokenInvalidException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
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
import java.util.UUID;

@Component
public class EmailVerificationTokenUtil {

    private static final String CLAIM_PURPOSE = "purpose";
    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_EMAIL_VERIFICATION_VERSION = "evv";
    private static final String PURPOSE_EMAIL_VERIFY = "email_verify";

    private final Key signingKey;
    private final String issuer;
    private final String audience;
    private final long expirationMinutes;

    public EmailVerificationTokenUtil(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.issuer}") String issuer,
            @Value("${security.jwt.audience}") String audience,
            @Value("${security.jwt.email-verification-expiration-minutes:15}") long expirationMinutes
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.issuer = issuer;
        this.audience = audience;
        this.expirationMinutes = expirationMinutes;
    }

    public String generateToken(UUID userId, String email, int emailVerificationVersion) {
        Instant now = Instant.now();
        Instant expiry = now.plus(expirationMinutes, ChronoUnit.MINUTES);

        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuer(issuer)
                .setAudience(audience)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .claim(CLAIM_PURPOSE, PURPOSE_EMAIL_VERIFY)
                .claim(CLAIM_EMAIL, email)
                .claim(CLAIM_EMAIL_VERIFICATION_VERSION, emailVerificationVersion)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public ParsedVerificationToken parseToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .requireIssuer(issuer)
                    .requireAudience(audience)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String purpose = claims.get(CLAIM_PURPOSE, String.class);
            if (!PURPOSE_EMAIL_VERIFY.equals(purpose)) {
                throw new VerificationTokenInvalidException();
            }

            String userId = claims.getSubject();
            String email = claims.get(CLAIM_EMAIL, String.class);
            Integer emailVerificationVersion = claims.get(CLAIM_EMAIL_VERIFICATION_VERSION, Integer.class);

            if (userId == null || email == null || emailVerificationVersion == null) {
                throw new VerificationTokenInvalidException();
            }

            return new ParsedVerificationToken(
                    UUID.fromString(userId),
                    email,
                    emailVerificationVersion
            );
        } catch (ExpiredJwtException ex) {
            throw new VerificationTokenExpiredException();
        } catch (VerificationTokenInvalidException ex) {
            throw ex;
        } catch (JwtException | IllegalArgumentException ex) {
            throw new VerificationTokenInvalidException();
        }
    }

    public record ParsedVerificationToken(UUID userId, String email, int emailVerificationVersion) {
    }
}
