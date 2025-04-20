package ru.alex.professionalslanguageapi.dto.game;

import lombok.Data;

@Data
public class Game {
    private String id;
    private Player player1;
    private Player player2;
    private GameStatus status;
    private GameData gameData;
    private Integer winnerPlayer;
    private Integer currentQuestion;
    private Boolean questionIsFinished;
    private long createdAt;
}
