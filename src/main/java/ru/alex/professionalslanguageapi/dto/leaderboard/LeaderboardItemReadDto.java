package ru.alex.professionalslanguageapi.dto.leaderboard;

public record LeaderboardItemReadDto(
        Integer userId,
        String userAvatar,
        String firstName,
        String lastName,
        Double score
) {
}
