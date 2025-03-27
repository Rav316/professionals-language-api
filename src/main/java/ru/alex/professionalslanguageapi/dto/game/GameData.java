package ru.alex.professionalslanguageapi.dto.game;

import java.util.List;

public record GameData (
        List<Question> questions
) {
}

