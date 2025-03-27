package ru.alex.professionalslanguageapi.dto.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {
     private Integer id;
     private String email;
     private Integer score;
     private Integer selectedAnswer;
     private Boolean answerIsRight;
}