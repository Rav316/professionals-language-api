package ru.alex.professionalslanguageapi.mapper.word;

import org.springframework.stereotype.Component;
import ru.alex.professionalslanguageapi.database.entity.Word;
import ru.alex.professionalslanguageapi.dto.word.WordListeningDto;
import ru.alex.professionalslanguageapi.mapper.ReadMapper;

@Component
public class WordListeningMapper extends ReadMapper<Word, WordListeningDto> {
    @Override
    public WordListeningDto toDto(Word entity) {
        return new WordListeningDto(
                entity.getWord(),
                entity.getTranscription()
        );
    }
}
