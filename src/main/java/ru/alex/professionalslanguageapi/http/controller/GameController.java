package ru.alex.professionalslanguageapi.http.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import ru.alex.professionalslanguageapi.dto.game.ConnectRequest;
import ru.alex.professionalslanguageapi.dto.game.Game;
import ru.alex.professionalslanguageapi.dto.game.GameData;
import ru.alex.professionalslanguageapi.dto.game.GamePlay;
import ru.alex.professionalslanguageapi.dto.game.IncrementScoreDto;
import ru.alex.professionalslanguageapi.dto.leaderboard.LeaderboardItemReadDto;
import ru.alex.professionalslanguageapi.service.GameService;

import java.util.List;
import java.util.Objects;

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

    @PostMapping("/start")
    public ResponseEntity<Game> start() {
        log.info("************* start game request");
        Game game = gameService.createGame();
        simpMessagingTemplate.convertAndSend("/topic/available-rooms", gameService.getAllAvailableRooms());
        return new ResponseEntity<>(game, OK);
    }

    @PostMapping("/connect")
    public ResponseEntity<Game> connect(@Validated @RequestBody ConnectRequest connectRequest) {
        log.info("************* connect request: {}", connectRequest);
        Game game = gameService.connectToGame(connectRequest.gameId());
        simpMessagingTemplate.convertAndSend("/topic/available-rooms", gameService.getAllAvailableRooms());
        return new ResponseEntity<>(game, OK);
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

    @PostMapping("/increment-score")
    public ResponseEntity<LeaderboardItemReadDto> incrementScore(@Validated @RequestBody IncrementScoreDto incrementScoreDto) {
        return new ResponseEntity<>(gameService.incrementScore(incrementScoreDto), OK);
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
