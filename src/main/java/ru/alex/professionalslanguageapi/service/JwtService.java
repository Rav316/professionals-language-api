package ru.alex.professionalslanguageapi.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.alex.professionalslanguageapi.database.entity.User;
import ru.alex.professionalslanguageapi.database.repository.UserRepository;
import ru.alex.professionalslanguageapi.dto.user.UserAuthDto;
import ru.alex.professionalslanguageapi.mapper.user.UserAuthMapper;

import java.time.ZonedDateTime;
import java.util.Date;

import static ru.alex.professionalslanguageapi.util.AuthUtils.getJwtFromAuthHeader;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class JwtService {
    @Value("${app.jwt.secret}")
    private String secret;


    @Value("${app.jwt.subject}")
    private String subject;

    @Value("${app.jwt.issuer}")
    private String issuer;

    @Value("${app.jwt.expiration}")
    private long expirationTime;

    @Value("${app.jwt.total-expiration}")
    private long totalExpirationTime;

    private final UserRepository userRepository;
    private final UserAuthMapper userAuthMapper;

    public String generateAuthToken(String email) throws JWTVerificationException {
        Date expirationDate = Date.from(ZonedDateTime.now().toInstant().plusMillis(expirationTime));
        return JWT.create()
                .withSubject(subject)
                .withClaim("email", email)
                .withIssuedAt(new Date())
                .withIssuer(issuer)
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC256(secret));
    }

    public String validateTokenAndRetrieveClaim(String token) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                .withSubject(subject)
                .withIssuer(issuer)
                .build();
        try {
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaim("email").asString();
        } catch (TokenExpiredException e) {
            throw e;
        }
        catch (JWTVerificationException e) {
            throw new JWTVerificationException("JWT token is not valid");
        }
    }

    public UserAuthDto refreshAuthToken(HttpServletRequest request) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                .withSubject(subject)
                .withIssuer(issuer)
                .build();

        DecodedJWT token;
        String authHeader = request.getHeader("Authorization");
        String authToken = getJwtFromAuthHeader(authHeader);
        try {
            token = verifier.verify(authToken);
        } catch (TokenExpiredException e) {
            token = JWT.decode(authToken);
            Date expirationDate = token.getExpiresAt();
            Date currentDate = new Date();

            long timeDifference = currentDate.getTime() - expirationDate.getTime();

            if(timeDifference > totalExpirationTime) {
                throw e;
            }
        }
        String email = token.getClaim("email").asString();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("user with email " + email + " not found"));
        String resultToken = generateAuthToken(token.getClaim("email").asString());
        return userAuthMapper.toDto(user, resultToken);
    }

}