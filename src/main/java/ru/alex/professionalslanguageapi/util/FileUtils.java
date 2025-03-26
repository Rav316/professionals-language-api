package ru.alex.professionalslanguageapi.util;

import jakarta.persistence.EntityNotFoundException;
import lombok.experimental.UtilityClass;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@UtilityClass
public class FileUtils {
    public static void uploadFile(MultipartFile file, String fileName, Path uploadPath) {
        try {
            if (Files.notExists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Files.copy(file.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex >= 0) {
            return fileName.substring(lastDotIndex + 1);
        }
        return "";
    }

    public static byte[] download(String directory, String fileName) {
        Path path = Path.of(directory, fileName);

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
