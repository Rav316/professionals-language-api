package ru.alex.professionalslanguageapi.http.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.alex.professionalslanguageapi.dto.user.UserDetailsDto;
import ru.alex.professionalslanguageapi.dto.user.UserReadDto;
import ru.alex.professionalslanguageapi.service.UserService;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping(value = "/profile/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<HttpStatus> upload(@RequestParam("file") MultipartFile file) {
        userService.upload(file);
        return new ResponseEntity<>(OK);
    }

    @GetMapping("/profile/avatar")
    public ResponseEntity<byte[]> download() {
        String fileName = ((UserDetailsDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).avatar();
        String extension = fileName.substring(fileName.lastIndexOf('.'));
        MediaType mediaType = MediaTypeFactory.getMediaType(extension)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(userService.download(fileName));
    }

    @GetMapping("/avatar/{fileName}")
    public ResponseEntity<byte[]> downloadByFileName(@PathVariable("fileName") String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf('.'));
        MediaType mediaType = MediaTypeFactory.getMediaType(extension)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(userService.download(fileName));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserReadDto> getCurrentUser() {
        return new ResponseEntity<>(userService.getCurrentUser(), OK);
    }
}
