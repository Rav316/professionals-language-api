package ru.alex.professionalslanguageapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.alex.professionalslanguageapi.database.repository.LeaderboardItemRepository;
import ru.alex.professionalslanguageapi.dto.leaderboard.LeaderboardItemReadDto;
import ru.alex.professionalslanguageapi.mapper.leaderboard.LeaderboardItemReadMapper;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LeaderboardItemService {
    private final LeaderboardItemRepository leaderboardItemRepository;
    private final LeaderboardItemReadMapper leaderboardItemReadMapper;

    public List<LeaderboardItemReadDto> findAll() {
        return leaderboardItemRepository.findAllWithUsers(Sort.by("id"))
                .stream()
                .map(leaderboardItemReadMapper::toDto)
                .toList();
    }
}
