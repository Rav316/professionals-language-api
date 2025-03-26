package ru.alex.professionalslanguageapi.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record UserRegisterDto(
        @NotNull
        @Email
        String email,
        @NotNull
        String firstName,
        @NotNull
        String lastName,
        @NotNull
        String password
) {
}
