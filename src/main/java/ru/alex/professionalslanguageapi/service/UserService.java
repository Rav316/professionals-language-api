package ru.alex.professionalslanguageapi.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.alex.professionalslanguageapi.database.entity.User;
import ru.alex.professionalslanguageapi.database.repository.UserRepository;
import ru.alex.professionalslanguageapi.mapper.user.UserDetailsMapper;
import ru.alex.professionalslanguageapi.util.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

import static ru.alex.professionalslanguageapi.util.AuthUtils.getAuthorizedUserId;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserDetailsMapper userDetailsMapper;

    @Value("${app.avatar-directory}")
    private String avatarDirectory;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(userDetailsMapper::toDto)
                .orElseThrow(() -> new UsernameNotFoundException("Failed to retrieve user: " + email));
    }


    @Transactional
    @Async
    public void upload(MultipartFile multipartFile) {
        String fileName = Objects.requireNonNull(multipartFile.getOriginalFilename());
        String newFileName = UUID.randomUUID() + "." + FileUtils.getFileExtension(fileName);
        Path uploadPath = Paths.get(avatarDirectory);
        FileUtils.uploadFile(multipartFile, newFileName, uploadPath);

        int userId = getAuthorizedUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("user with id " + userId + " not found"));
        user.setAvatar(newFileName);
        userRepository.save(user);
    }

    public byte[] download(String fileName) {
        Path path = Path.of(avatarDirectory, fileName);

        try {
            if(!Files.exists(path)) {
                throw new IOException();
            }
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new EntityNotFoundException("file " + fileName + " not found");
        }
    }
}
