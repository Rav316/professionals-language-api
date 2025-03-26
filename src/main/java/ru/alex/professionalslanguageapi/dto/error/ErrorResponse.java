package ru.alex.professionalslanguageapi.dto.error;

import java.time.LocalDateTime;

public record ErrorResponse(
        LocalDateTime date,
        Object message
) {
}
