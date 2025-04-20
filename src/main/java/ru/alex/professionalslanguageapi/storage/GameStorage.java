package ru.alex.professionalslanguageapi.storage;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.alex.professionalslanguageapi.dto.game.Game;
import ru.alex.professionalslanguageapi.dto.game.GameStatus;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Data
public class GameStorage {
    public Map<String, Game> games = new HashMap<>();

    public void setGame(Game game) {
        games.put(game.getId(), game);
    }

    public List<String> getAllAvailableRooms() {
        return games.entrySet()
                .stream()
                .filter(e -> e.getValue().getStatus() == GameStatus.NEW)
                .sorted(Comparator.comparingLong(e -> e.getValue().getCreatedAt()))
                .map(Map.Entry::getKey)
                .toList();
    }
}
