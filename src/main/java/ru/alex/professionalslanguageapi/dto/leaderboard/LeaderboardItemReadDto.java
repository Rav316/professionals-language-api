package ru.alex.professionalslanguageapi.dto.leaderboard;

public record LeaderboardItemReadDto(
        Integer userId,
        String userAvatar,
        Integer score
) {
}
