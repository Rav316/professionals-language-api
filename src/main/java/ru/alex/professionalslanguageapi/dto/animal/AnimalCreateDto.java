package ru.alex.professionalslanguageapi.dto.animal;

import jakarta.validation.constraints.NotNull;

public record AnimalCreateDto(
        @NotNull
        String name
) {
}
