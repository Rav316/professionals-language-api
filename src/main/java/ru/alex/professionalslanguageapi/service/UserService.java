package ru.alex.professionalslanguageapi.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.alex.professionalslanguageapi.database.entity.User;
import ru.alex.professionalslanguageapi.database.repository.UserRepository;
import ru.alex.professionalslanguageapi.dto.user.UserDetailsDto;
import ru.alex.professionalslanguageapi.dto.user.UserReadDto;
import ru.alex.professionalslanguageapi.mapper.user.UserDetailsMapper;
import ru.alex.professionalslanguageapi.util.FileUtils;

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

    @Value("${app.directory.avatar}")
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
        return FileUtils.download(avatarDirectory, fileName);
    }

    public UserReadDto getCurrentUser() {
        UserDetailsDto userDetailsDto = ((UserDetailsDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return new UserReadDto(
                userDetailsDto.id(),
                userDetailsDto.email(),
                userDetailsDto.firstName(),
                userDetailsDto.lastName(),
                userDetailsDto.avatar()
        );
    }
}
