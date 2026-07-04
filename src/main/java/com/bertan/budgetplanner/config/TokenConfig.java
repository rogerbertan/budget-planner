package com.bertan.budgetplanner.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bertan.budgetplanner.domain.user.Role;
import com.bertan.budgetplanner.domain.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
public class TokenConfig {

    @Value("${jwt.secret}")
    private String secret;

    public String generateToken(User user) {

        Algorithm algorithm = Algorithm.HMAC256(secret);

        return JWT.create()
                .withClaim("userId", user.getId())
                .withClaim("role", user.getRole().name())
                .withSubject(user.getUsername())
                .withExpiresAt(Instant.now().plusSeconds(86400))
                .withIssuedAt(Instant.now())
                .sign(algorithm);
    }

    public Optional<JWTUserData> validateToken(String token) {

        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            DecodedJWT decode = JWT.require(algorithm)
                    .build()
                    .verify(token);

            return Optional.of(new JWTUserData(
                    decode.getClaim("userId").asLong(),
                    decode.getSubject(),
                    Role.valueOf(decode.getClaim("role").asString())));

        } catch (JWTVerificationException e) {
            return Optional.empty();
        }
    }
}
