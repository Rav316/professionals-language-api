package ru.alex.professionalslanguageapi.dto.user;

import jakarta.validation.constraints.NotNull;

public record UserLoginDto(
        @NotNull
        String email,
        @NotNull
        String password
) {
}
