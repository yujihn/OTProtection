package com.mHide.otpService.dto;

import com.mHide.otpService.model.user.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

/**
 * DTO for {@link com.mHide.otpservice.model.user.User}
 */
@Builder
public record UserDto(
        @NotNull @NotBlank String username,
        @NotNull @NotBlank String password,
        @NotNull UserRole role,
        @Pattern(regexp = "^(\\+7|8)\\d{10}$", message = "Телефон должен начинаться с +7 или 8 и содержать 11 цифр")
        String phoneNumber,
        @Email(message = "Некорректный формат почты")
        String email,
        @Positive Long telegramId) {
}