package ru.alex.professionalslanguageapi.http.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.alex.professionalslanguageapi.dto.game.ConnectRequest;
import ru.alex.professionalslanguageapi.dto.game.Game;
import ru.alex.professionalslanguageapi.dto.game.GameData;
import ru.alex.professionalslanguageapi.dto.game.GamePlay;
import ru.alex.professionalslanguageapi.service.GameService;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/game")
@Slf4j
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping
    public ResponseEntity<GameData> generateGameData() {
        return new ResponseEntity<>(gameService.generateGameData(), OK);
    }

    @PostMapping("/start")
    public ResponseEntity<Game> start() {
        log.info("************* start game request");
        return new ResponseEntity<>(gameService.createGame(), OK);
    }

    @PostMapping("/connect")
    public ResponseEntity<Game> connect(@Validated @RequestBody ConnectRequest connectRequest) {
        log.info("************* connect request: {}", connectRequest);
        return new ResponseEntity<>(gameService.connectToGame(connectRequest.gameId()), OK);
    }

    @PostMapping("/gameplay")
    public ResponseEntity<Game> gamePlay(@RequestBody GamePlay request) {
        log.info("************* gameplay: {}", request);
        Game game = gameService.gamePlay(request);
        simpMessagingTemplate.convertAndSend("/topic/game-progress/" + game.getId(), game);
        return new ResponseEntity<>(game, OK);
    }

    @PostMapping("/next-question")
    public ResponseEntity<Game> nextQuestion(@Validated @RequestBody ConnectRequest connectRequest) {
        log.info("************* next question: {}", connectRequest);
        Game game = gameService.nextQuestion(connectRequest.gameId());
        simpMessagingTemplate.convertAndSend("/topic/game-progress/" + game.getId(), game);
        return new ResponseEntity<>(game, OK);
    }
}
