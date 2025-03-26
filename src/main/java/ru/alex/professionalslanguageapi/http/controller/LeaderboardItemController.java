package ru.alex.professionalslanguageapi.http.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.alex.professionalslanguageapi.dto.leaderboard.LeaderboardItemReadDto;
import ru.alex.professionalslanguageapi.service.LeaderboardItemService;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
public class LeaderboardItemController {
    private final LeaderboardItemService leaderboardItemService;

    @GetMapping
    public List<LeaderboardItemReadDto> findAll() {
        return leaderboardItemService.findAll();
    }
}
