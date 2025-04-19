package ru.alex.professionalslanguageapi.dto.game;

import jakarta.validation.constraints.NotNull;

public record GamePlay(
        @NotNull
        Integer selectedAnswer
) {
}
