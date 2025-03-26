package ru.alex.professionalslanguageapi.http.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.alex.professionalslanguageapi.dto.user.UserAuthDto;
import ru.alex.professionalslanguageapi.dto.user.UserLoginDto;
import ru.alex.professionalslanguageapi.dto.user.UserRegisterDto;
import ru.alex.professionalslanguageapi.service.AuthService;
import ru.alex.professionalslanguageapi.util.ExceptionUtils;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserAuthDto> performRegistration(@RequestBody @Validated UserRegisterDto user) {
        try {
            return new ResponseEntity<>(authService.register(user), CREATED);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(
                    BAD_REQUEST,
                    ExceptionUtils.getSqlExceptionMessage(ex)
            );
        }
    }

    @PostMapping("/login")
    public ResponseEntity<UserAuthDto> login(@RequestBody @Validated UserLoginDto user) {
        return new ResponseEntity<>(authService.login(user), OK);
    }

    @GetMapping("/refresh-token")
    public ResponseEntity<UserAuthDto> refreshAuthToken(HttpServletRequest request) {
        return new ResponseEntity<>(authService.refreshAuthToken(request), OK);
    }
}

