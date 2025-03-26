package ru.alex.professionalslanguageapi.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.alex.professionalslanguageapi.database.entity.Animal;
import ru.alex.professionalslanguageapi.database.repository.AnimalRepository;
import ru.alex.professionalslanguageapi.dto.animal.AnimalCreateDto;
import ru.alex.professionalslanguageapi.dto.animal.AnimalReadDto;
import ru.alex.professionalslanguageapi.mapper.animal.AnimalCreateMapper;
import ru.alex.professionalslanguageapi.mapper.animal.AnimalReadMapper;
import ru.alex.professionalslanguageapi.util.FileUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AnimalService {
    private final AnimalRepository animalRepository;
    private final AnimalReadMapper animalReadMapper;
    private final AnimalCreateMapper animalCreateMapper;

    @Value("${app.directory.exercise}")
    private String exerciseDirectory;

    public List<AnimalReadDto> findAll() {
        return animalRepository.findAll(Sort.by("id"))
                .stream()
                .map(animalReadMapper::toDto)
                .toList();
    }

    @Transactional
    @Async
    public void upload(Integer id, MultipartFile multipartFile) {
        String fileName = Objects.requireNonNull(multipartFile.getOriginalFilename());
        String newFileName = UUID.randomUUID() + "." + FileUtils.getFileExtension(fileName);
        Path uploadPath = Paths.get(exerciseDirectory);
        FileUtils.uploadFile(multipartFile, newFileName, uploadPath);

        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("exercise with id " + id + " not found"));
        animal.setImage(newFileName);
        animalRepository.save(animal);
    }

    @Transactional
    public AnimalReadDto create(AnimalCreateDto animalCreateDto) {
        Animal animal = animalCreateMapper.toEntity(animalCreateDto);
        return animalReadMapper.toDto(animalRepository.save(animal));
    }

    @Transactional
    public void delete(Integer id) {
        animalRepository.deleteById(id);
    }

    public byte[] download(String fileName) {
        return FileUtils.download(exerciseDirectory, fileName);
    }
}
