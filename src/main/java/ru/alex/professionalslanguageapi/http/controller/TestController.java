package ru.alex.professionalslanguageapi.http.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/test")
public class TestController {
    @GetMapping
    public ResponseEntity<HttpStatus> test() {
        return new ResponseEntity<>(OK);
    }
}
