package com.mHide.otpService.service.otp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class FileStorageService {
    private final String storagePath;

    public FileStorageService(@Value("${otp.file.storage-path:./otp-storage}") String storagePath) {
        this.storagePath = storagePath;
        createStorageDirectory();
    }

    private void createStorageDirectory() {
        try {
            Path path = Paths.get(storagePath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("Created OTP storage directory: {}", path.toAbsolutePath());
            }
        } catch (IOException e) {
            log.error("Failed to create OTP storage directory", e);
            throw new RuntimeException("Failed to initialize OTP file storage", e);
        }
    }

    public void appendToFile(String filename, String content) throws IOException {
        Path filePath = Paths.get(storagePath, filename);
        Files.write(filePath,
                content.getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND);
        log.debug("Appended content to file: {}", filePath);
    }

    public List<String> readFileContent(String filename) throws IOException {
        Path filePath = Paths.get(storagePath, filename);
        if (Files.exists(filePath)) {
            return Files.readAllLines(filePath);
        }
        return Collections.emptyList();
    }

    public void clearFile(String filename) throws IOException {
        Path filePath = Paths.get(storagePath, filename);
        Files.deleteIfExists(filePath);
        log.info("Cleared OTP file: {}", filename);
    }
}