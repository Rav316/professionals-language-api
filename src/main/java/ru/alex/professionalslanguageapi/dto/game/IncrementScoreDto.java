package ru.alex.professionalslanguageapi.dto.game;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record IncrementScoreDto(
        @NotNull
        Integer userId,
        @Min(0)
        @NotNull
        Integer score
) {
}
