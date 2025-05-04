package com.mHide.otpService.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

@Validated
@ConfigurationProperties(prefix = "otp.file")
public record FileStorageProperties(
        @NotBlank String storagePath,
        @NotBlank String filename,
        String maxFileSize,
        int maxBackupFiles
) {
    public FileStorageProperties {
        if (maxBackupFiles < 0) {
            throw new IllegalArgumentException("maxBackupFiles must be positive");
        }
    }
}