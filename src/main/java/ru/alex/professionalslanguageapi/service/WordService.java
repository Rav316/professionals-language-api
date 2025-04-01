package ru.alex.professionalslanguageapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.alex.professionalslanguageapi.database.repository.WordRepository;
import ru.alex.professionalslanguageapi.dto.word.WordListeningDto;
import ru.alex.professionalslanguageapi.mapper.word.WordListeningMapper;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WordService {
    private final WordRepository wordRepository;
    private final WordListeningMapper wordListeningMapper;

    public WordListeningDto getRandomWord() {
        return wordListeningMapper.toDto(wordRepository.getRandomWord());
    }
}
