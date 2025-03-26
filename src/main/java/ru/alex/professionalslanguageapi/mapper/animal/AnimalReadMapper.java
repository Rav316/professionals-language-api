package ru.alex.professionalslanguageapi.mapper.animal;

import org.springframework.stereotype.Component;
import ru.alex.professionalslanguageapi.database.entity.Animal;
import ru.alex.professionalslanguageapi.dto.animal.AnimalReadDto;
import ru.alex.professionalslanguageapi.mapper.ReadMapper;

@Component
public class AnimalReadMapper extends ReadMapper<Animal, AnimalReadDto> {
    @Override
    public AnimalReadDto toDto(Animal entity) {
        return new AnimalReadDto(
                entity.getId(),
                entity.getName(),
                entity.getImage()
        );
    }
}
