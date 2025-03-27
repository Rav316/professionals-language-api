package ru.alex.professionalslanguageapi.storage;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.alex.professionalslanguageapi.dto.game.Game;

import java.util.HashMap;
import java.util.Map;

@Component
@Data
public class GameStorage {
    public Map<String, Game> games = new HashMap<>();

    public void setGame(Game game) {
        games.put(game.getId(), game);
    }
}
