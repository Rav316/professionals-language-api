package ru.alex.professionalslanguageapi.util;

import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AuthUtils {
    public static String getJwtFromAuthHeader(String authHeader) {
        if (authHeader == null || authHeader.isBlank() || !authHeader.startsWith("Bearer")) {
            throw new JWTVerificationException("JWT token is not valid");
        }

        String jwt = authHeader.substring(7);
        if (jwt.isBlank()) {
            throw new JWTVerificationException("JWT token is not valid");
        }
        return jwt;
    }
}
