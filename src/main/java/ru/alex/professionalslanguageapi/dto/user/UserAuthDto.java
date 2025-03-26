package ru.alex.professionalslanguageapi.dto.user;

public record UserAuthDto(
        Integer id,
        String email,
        String firstName,
        String lastName,
        String avatar,
        String token
) {
}
