package ru.alex.professionalslanguageapi.http.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import ru.alex.professionalslanguageapi.dto.game.Game;
import ru.alex.professionalslanguageapi.dto.game.GameData;
import ru.alex.professionalslanguageapi.dto.game.GamePlay;
import ru.alex.professionalslanguageapi.service.GameService;

import java.util.List;
import java.util.Objects;

import static org.springframework.http.HttpStatus.NO_CONTENT;
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

    @GetMapping("/available-rooms")
    public List<String> getAllAvailableRooms() {
        return gameService.getAllAvailableRooms();
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<HttpStatus> getGame(@PathVariable("gameId") String gameId) {
        Game game = gameService.getGame(gameId);
        simpMessagingTemplate.convertAndSend("/topic/game-progress/" + gameId, game);
        return new ResponseEntity<>(OK);
    }

    @PostMapping("/start")
    public ResponseEntity<Game> start() {
        log.info("************* start game request");
        Game game = gameService.createGame();
        simpMessagingTemplate.convertAndSend("/topic/available-rooms", gameService.getAllAvailableRooms());
//        simpMessagingTemplate.convertAndSend("/topic/game-progress/" + game.getId(), game);
        return new ResponseEntity<>(game, OK);
    }

    @PostMapping("/{gameId}/connect")
    public ResponseEntity<Game> connect(@PathVariable("gameId") String gameId) {
        log.info("************* connect request: {}", gameId);
        Game game = gameService.connectToGame(gameId);
        simpMessagingTemplate.convertAndSend("/topic/available-rooms", gameService.getAllAvailableRooms());
//        simpMessagingTemplate.convertAndSend("/topic/game-progress/" + game.getId(), game);
        return new ResponseEntity<>(game, OK);
    }

    @PostMapping("/{gameId}/gameplay")
    public ResponseEntity<Game> gamePlay(@PathVariable("gameId") String gameId,
                                         @Validated @RequestBody GamePlay request) {
        log.info("************* gameplay: {}", request);
        Game game = gameService.gamePlay(gameId, request.selectedAnswer());
        simpMessagingTemplate.convertAndSend("/topic/game-progress/" + game.getId(), game);
        return new ResponseEntity<>(game, OK);
    }

    @PostMapping("/{gameId}/next-question")
    public ResponseEntity<Game> nextQuestion(@PathVariable("gameId") String gameId) {
        log.info("************* next question: {}", gameId);
        Game game = gameService.nextQuestion(gameId);
        simpMessagingTemplate.convertAndSend("/topic/game-progress/" + game.getId(), game);
        return new ResponseEntity<>(game, OK);
    }

    @DeleteMapping("/{gameId}/cancel")
    public ResponseEntity<HttpStatus> cancelGame(@PathVariable("gameId") String gameId) {
        gameService.cancelGame(gameId);
        log.info("************* game cancelled: {}", gameId);
        return new ResponseEntity<>(NO_CONTENT);
    }

    @EventListener
    public void handleSubscription(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = accessor.getDestination();
        if(Objects.equals(destination, "/topic/available-rooms")) {
            simpMessagingTemplate.convertAndSend(destination, getAllAvailableRooms());
        }
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("username");
        log.info("************* player disconnected: {}", username);
        if(username != null) {

        }
    }
}
