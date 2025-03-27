package ru.alex.professionalslanguageapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.alex.professionalslanguageapi.database.repository.WordRepository;
import ru.alex.professionalslanguageapi.dto.game.GameData;
import ru.alex.professionalslanguageapi.mapper.game.GameDataMapper;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GameService {
    private final WordRepository wordRepository;
    private final GameDataMapper gameDataMapper;

    public GameData generateGameData() {
        return gameDataMapper.toDto(wordRepository.fetchGameDataRaw());
    }
}
