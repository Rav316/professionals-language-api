package ru.alex.professionalslanguageapi.dto.game;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class Question {
    Integer wordId;
    private String word;
    private String transcription;
    @Builder.Default
    private List<String> answers = new ArrayList<>();
    private Integer correctAnswerNumber;
}