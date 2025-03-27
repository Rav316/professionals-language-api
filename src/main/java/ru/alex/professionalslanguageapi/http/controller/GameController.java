package ru.alex.professionalslanguageapi.http.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.alex.professionalslanguageapi.dto.game.GameData;
import ru.alex.professionalslanguageapi.service.GameService;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;

    @GetMapping
    public ResponseEntity<GameData> generateGameData() {
        return new ResponseEntity<>(gameService.generateGameData(), OK);
    }
}
