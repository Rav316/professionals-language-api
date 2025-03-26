package ru.alex.professionalslanguageapi.dto.user;

public record UserReadDto(
        Integer id,
        String email,
        String firstName,
        String lastName,
        String avatar
) {
}
