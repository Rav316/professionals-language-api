package ru.alex.professionalslanguageapi.util;

import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.alex.professionalslanguageapi.dto.user.UserDetailsDto;

@UtilityClass
public class AuthUtils {
    public static int getAuthorizedUserId() {
        UserDetailsDto principal = (UserDetailsDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.id();
    }

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
