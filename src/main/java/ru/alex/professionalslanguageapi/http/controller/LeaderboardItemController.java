package ru.alex.professionalslanguageapi.http.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.alex.professionalslanguageapi.dto.game.IncrementScoreDto;
import ru.alex.professionalslanguageapi.dto.leaderboard.LeaderboardItemReadDto;
import ru.alex.professionalslanguageapi.service.LeaderboardItemService;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
public class LeaderboardItemController {
    private final LeaderboardItemService leaderboardItemService;

    @GetMapping
    public List<LeaderboardItemReadDto> findAll() {
        return leaderboardItemService.findAll();
    }

    @PostMapping("/increment-score")
    public ResponseEntity<LeaderboardItemReadDto> incrementScore(@Validated @RequestBody IncrementScoreDto incrementScoreDto) {
        return new ResponseEntity<>(leaderboardItemService.incrementScore(incrementScoreDto), OK);
    }
}
