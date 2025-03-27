package ru.alex.professionalslanguageapi.mapper.leaderboard;

import org.springframework.stereotype.Component;
import ru.alex.professionalslanguageapi.database.entity.LeaderboardItem;
import ru.alex.professionalslanguageapi.database.entity.User;
import ru.alex.professionalslanguageapi.dto.leaderboard.LeaderboardItemReadDto;
import ru.alex.professionalslanguageapi.mapper.ReadMapper;

@Component
public class LeaderboardItemReadMapper extends ReadMapper<LeaderboardItem, LeaderboardItemReadDto> {
    @Override
    public LeaderboardItemReadDto toDto(LeaderboardItem entity) {
        User user = entity.getUser();
        return new LeaderboardItemReadDto(
                user.getId(),
                user.getAvatar(),
                user.getFirstName(),
                user.getLastName(),
                entity.getScore()
        );
    }
}
