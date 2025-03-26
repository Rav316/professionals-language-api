package ru.alex.professionalslanguageapi.dto.exercise;

import jakarta.validation.constraints.NotNull;

public record ExerciseCreateDto(
        @NotNull
        String name
) {
}
