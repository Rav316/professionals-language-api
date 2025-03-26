package ru.alex.professionalslanguageapi.mapper.exercise;

import org.springframework.stereotype.Component;
import ru.alex.professionalslanguageapi.database.entity.Exercise;
import ru.alex.professionalslanguageapi.dto.exercise.ExerciseReadDto;
import ru.alex.professionalslanguageapi.mapper.ReadMapper;

@Component
public class ExerciseReadMapper extends ReadMapper<Exercise, ExerciseReadDto> {
    @Override
    public ExerciseReadDto toDto(Exercise entity) {
        return new ExerciseReadDto(
                entity.getId(),
                entity.getName(),
                entity.getImage()
        );
    }
}
