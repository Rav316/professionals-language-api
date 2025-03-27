package ru.alex.professionalslanguageapi.mapper.game;

import org.springframework.stereotype.Component;
import ru.alex.professionalslanguageapi.dto.game.GameData;
import ru.alex.professionalslanguageapi.dto.game.Question;
import ru.alex.professionalslanguageapi.mapper.ReadMapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class GameDataMapper extends ReadMapper<List<Object[]>, GameData> {
    @Override
    public GameData toDto(List<Object[]> entity) {
        Map<Integer, Question> questionsMap = new LinkedHashMap<>();

        for (Object[] row : entity) {
            Integer wordId = ((Number) row[0]).intValue();
            String word = (String) row[1];
            String transcription = (String) row[2];
            String translation = (String) row[3];

            Question question = questionsMap.computeIfAbsent(wordId, id ->
                    Question.builder().wordId(id).word(word).transcription(transcription).build()
            );

            question.getAnswers().add(translation);
        }

        // Определяем правильный ответ
        int counter = 0;
        for (Question question : questionsMap.values()) {
            // Находим правильный ответ и устанавливаем его номер
            for (int i = 0; i < question.getAnswers().size(); i++) {
                if (entity.get(counter)[4].equals(true)) {  // проверка правильности ответа
                    question.setCorrectAnswerNumber(i);
                }
                counter++;
            }
        }

        return new GameData(new ArrayList<>(questionsMap.values()));
    }
}
