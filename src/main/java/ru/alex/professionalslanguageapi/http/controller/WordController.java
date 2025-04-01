package ru.alex.professionalslanguageapi.http.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.alex.professionalslanguageapi.dto.word.WordListeningDto;
import ru.alex.professionalslanguageapi.service.WordService;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/words")
@RequiredArgsConstructor
public class WordController {
    private final WordService wordService;

    @GetMapping("/random")
    public ResponseEntity<WordListeningDto> getRandomWord() {
        return new ResponseEntity<>(wordService.getRandomWord(), OK);
    }
}
