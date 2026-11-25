package learning.event_ticketing_and_booking_api.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JWTServiceTest {
    private static final String SECRET = Base64.getEncoder().encodeToString(
            "01234567890123456789012345678901".getBytes(StandardCharsets.UTF_8));
    private static final long EXPIRATION_MILLIS = Duration.ofMinutes(30).toMillis();

    @Test
    void tokenRemainsValidWhenServiceRestartsWithSameSecret() {
        JWTService issuingService = new JWTService(SECRET, EXPIRATION_MILLIS);
        String token = issuingService.generateToken("attendee");

        JWTService restartedService = new JWTService(SECRET, EXPIRATION_MILLIS);

        assertTrue(restartedService.validateToken(token, userDetails("attendee")));
    }

    @Test
    void tokenIsRejectedWhenSigningSecretChanges() {
        JWTService issuingService = new JWTService(SECRET, EXPIRATION_MILLIS);
        String token = issuingService.generateToken("attendee");
        String differentSecret = Base64.getEncoder().encodeToString(
                "abcdefghijklmnopqrstuvwxyz123456".getBytes(StandardCharsets.UTF_8));
        JWTService restartedService = new JWTService(differentSecret, EXPIRATION_MILLIS);

        assertThrows(JwtException.class,
                () -> restartedService.validateToken(token, userDetails("attendee")));
    }

    @Test
    void tokenUsesConfiguredThirtyMinuteExpiration() {
        JWTService jwtService = new JWTService(SECRET, EXPIRATION_MILLIS);
        String token = jwtService.generateToken("attendee");
        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET)))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals(EXPIRATION_MILLIS, claims.getExpiration().getTime() - claims.getIssuedAt().getTime());
    }

    private UserDetails userDetails(String username) {
        return User.withUsername(username)
                .password("not-used")
                .authorities("ROLE_ATTENDEE")
                .build();
    }
}
