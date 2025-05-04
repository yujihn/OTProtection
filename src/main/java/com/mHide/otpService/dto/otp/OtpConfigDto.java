package com.mHide.otpService.dto.otp;

/**
 * DTO for {@link com.mHide.otpservice.model.otp.OtpConfig}
 */
public record OtpConfigDto(Integer codeLength, Long ttlSeconds) {
}