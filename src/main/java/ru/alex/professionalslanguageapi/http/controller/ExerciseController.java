package ru.alex.professionalslanguageapi.http.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import ru.alex.professionalslanguageapi.dto.exercise.ExerciseCreateDto;
import ru.alex.professionalslanguageapi.dto.exercise.ExerciseReadDto;
import ru.alex.professionalslanguageapi.service.ExerciseService;
import ru.alex.professionalslanguageapi.util.ExceptionUtils;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
public class ExerciseController {
    private final ExerciseService exerciseService;

    @GetMapping
    public List<ExerciseReadDto> findAll() {
        return exerciseService.findAll();
    }

    @PostMapping
    public ResponseEntity<ExerciseReadDto> create(@Validated @RequestBody ExerciseCreateDto exerciseCreateDto) {
        try {
            return new ResponseEntity<>(exerciseService.create(exerciseCreateDto), CREATED);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(
                    BAD_REQUEST,
                    ExceptionUtils.getSqlExceptionMessage(ex)
            );
        }
    }

    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<HttpStatus> upload(@PathVariable("id") Integer id, @RequestParam("file") MultipartFile file) {
        exerciseService.upload(id, file);
        return new ResponseEntity<>(OK);
    }

    @GetMapping("/image/{fileName}")
    public ResponseEntity<byte[]> download(@PathVariable("fileName") String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf('.'));
        MediaType mediaType = MediaTypeFactory.getMediaType(extension)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(exerciseService.download(fileName));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") Integer id) {
        exerciseService.delete(id);
        return new ResponseEntity<>(NO_CONTENT);
    }
}
