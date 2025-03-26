package ru.alex.professionalslanguageapi.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.alex.professionalslanguageapi.database.entity.Exercise;
import ru.alex.professionalslanguageapi.database.repository.ExerciseRepository;
import ru.alex.professionalslanguageapi.dto.exercise.ExerciseCreateDto;
import ru.alex.professionalslanguageapi.dto.exercise.ExerciseReadDto;
import ru.alex.professionalslanguageapi.mapper.exercise.ExerciseCreateMapper;
import ru.alex.professionalslanguageapi.mapper.exercise.ExerciseReadMapper;
import ru.alex.professionalslanguageapi.util.FileUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final ExerciseReadMapper exerciseReadMapper;
    private final ExerciseCreateMapper exerciseCreateMapper;

    @Value("${app.directory.exercise}")
    private String exerciseDirectory;

    public List<ExerciseReadDto> findAll() {
        return exerciseRepository.findAll()
                .stream()
                .map(exerciseReadMapper::toDto)
                .toList();
    }

    @Transactional
    @Async
    public void upload(Integer id, MultipartFile multipartFile) {
        String fileName = Objects.requireNonNull(multipartFile.getOriginalFilename());
        String newFileName = UUID.randomUUID() + "." + FileUtils.getFileExtension(fileName);
        Path uploadPath = Paths.get(exerciseDirectory);
        FileUtils.uploadFile(multipartFile, newFileName, uploadPath);

        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("exercise with id " + id + " not found"));
        exercise.setImage(newFileName);
        exerciseRepository.save(exercise);
    }

    @Transactional
    public ExerciseReadDto create(ExerciseCreateDto exerciseCreateDto) {
        Exercise exercise = exerciseCreateMapper.toEntity(exerciseCreateDto);
        return exerciseReadMapper.toDto(exerciseRepository.save(exercise));
    }

    @Transactional
    public void delete(Integer id) {
        exerciseRepository.deleteById(id);
    }

    public byte[] download(String fileName) {
        return FileUtils.download(exerciseDirectory, fileName);
    }
}
