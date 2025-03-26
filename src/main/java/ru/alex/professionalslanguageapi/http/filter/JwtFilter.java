package ru.alex.professionalslanguageapi.http.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.alex.professionalslanguageapi.dto.error.ErrorResponse;
import ru.alex.professionalslanguageapi.service.JwtService;
import ru.alex.professionalslanguageapi.service.UserService;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.GONE;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static ru.alex.professionalslanguageapi.util.AuthUtils.getJwtFromAuthHeader;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        try {
            String jwt = getJwtFromAuthHeader(authHeader);

            String username = jwtService.validateTokenAndRetrieveClaim(jwt);

            UserDetails userDetails = userService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    userDetails.getPassword(),
                    userDetails.getAuthorities()
            );

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

            filterChain.doFilter(request, response);
        } catch (TokenExpiredException ex) {
            setErrorResponse(response, GONE, ex);
        } catch (JWTVerificationException | UsernameNotFoundException ex) {
            setErrorResponse(response, UNAUTHORIZED, ex);
        }
    }


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/api/auth") ||
                request.getRequestURI().startsWith("/api/version") ||
                request.getRequestURI().startsWith("/swagger-ui") ||
                request.getRequestURI().startsWith("/v3/api-docs");
    }

    private void setErrorResponse(HttpServletResponse response, HttpStatus status, Throwable ex) {
        response.setStatus(status.value());
        response.setContentType("application/json");

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                ex.getMessage()
        );
        try (PrintWriter writer = response.getWriter()) {
            String json = objectMapper.writeValueAsString(errorResponse);
            if(writer != null) {
                writer.write(json);
            } else {
                response.getOutputStream().write(json.getBytes());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
