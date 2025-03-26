package ru.alex.professionalslanguageapi.mapper.exercise;

import org.springframework.stereotype.Component;
import ru.alex.professionalslanguageapi.database.entity.Exercise;
import ru.alex.professionalslanguageapi.dto.exercise.ExerciseCreateDto;
import ru.alex.professionalslanguageapi.mapper.CreateMapper;

@Component
public class ExerciseCreateMapper extends CreateMapper<Exercise, ExerciseCreateDto> {

    @Override
    public Exercise toEntity(ExerciseCreateDto dto) {
        Exercise exercise = new Exercise();
        exercise.setName(dto.name());

        return exercise;
    }
}
