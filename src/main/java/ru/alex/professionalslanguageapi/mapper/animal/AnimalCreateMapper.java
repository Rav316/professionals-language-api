package ru.alex.professionalslanguageapi.mapper.animal;

import org.springframework.stereotype.Component;
import ru.alex.professionalslanguageapi.database.entity.Animal;
import ru.alex.professionalslanguageapi.dto.animal.AnimalCreateDto;
import ru.alex.professionalslanguageapi.mapper.CreateMapper;

@Component
public class AnimalCreateMapper extends CreateMapper<Animal, AnimalCreateDto> {

    @Override
    public Animal toEntity(AnimalCreateDto dto) {
        Animal animal = new Animal();
        animal.setName(dto.name());

        return animal;
    }
}
