package ru.alex.professionalslanguageapi.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.alex.professionalslanguageapi.database.entity.LeaderboardItem;
import ru.alex.professionalslanguageapi.database.repository.LeaderboardItemRepository;
import ru.alex.professionalslanguageapi.dto.game.IncrementScoreDto;
import ru.alex.professionalslanguageapi.dto.leaderboard.LeaderboardItemReadDto;
import ru.alex.professionalslanguageapi.mapper.leaderboard.LeaderboardItemReadMapper;

import java.util.List;

import static ru.alex.professionalslanguageapi.util.AuthUtils.getAuthorizedUserId;

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

    @Transactional
    public LeaderboardItemReadDto incrementScore(IncrementScoreDto incrementScoreDto) {
        int authorizedUserId = getAuthorizedUserId();
        LeaderboardItem leaderboardItem = leaderboardItemRepository.findByUser(authorizedUserId)
                .orElseThrow(() -> new EntityNotFoundException("user with id " + authorizedUserId + " not found"));
        leaderboardItem.setScore(leaderboardItem.getScore() + incrementScoreDto.score());
        return leaderboardItemReadMapper.toDto(leaderboardItemRepository.save(leaderboardItem));
    }
}
