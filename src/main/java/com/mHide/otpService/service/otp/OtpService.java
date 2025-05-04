package com.mHide.otpService.service.otp;

import com.mHide.otpService.dto.otp.GenerateCodeRequestDto;
import com.mHide.otpService.dto.otp.ValidateCodeRequestDto;
import com.mHide.otpService.exception.OtpCodeExpiredException;
import com.mHide.otpService.exception.OtpCodeNotActiveException;
import com.mHide.otpService.model.otp.OtpCode;
import com.mHide.otpService.model.otp.OtpConfig;
import com.mHide.otpService.model.otp.OtpStatus;
import com.mHide.otpService.model.user.User;
import com.mHide.otpService.repository.OtpCodeRepository;
import com.mHide.otpService.service.UserService;
import com.mHide.otpService.service.notification.NotificationSender;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpConfigService otpConfigService;
    private final UserService userService;
    private final OtpCodeRepository otpCodeRepository;
    private final List<NotificationSender> notificationSenders;
    private final FileStorageService fileStorageService;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public String generateOtpCode(GenerateCodeRequestDto requestDto) {
        OtpConfig cfg = otpConfigService.getOtpConfig();
        String code = generateRandomCode(cfg.getCodeLength());
        User user = userService.getAuthorizedUser();

        OtpCode otpCode = OtpCode.builder()
                .code(code)
                .user(user)
                .status(OtpStatus.ACTIVE)
                .operationId(requestDto.operationId())
                .expiresAt(LocalDateTime.now().plusSeconds(cfg.getTtlSeconds()))
                .build();

        otpCodeRepository.save(otpCode);
        log.info("Generated OTP code: {}, user: {}", code, user.getUsername());

        saveOtpToFile(code, user, requestDto.operationId());
        sendNotifications(code, user);

        return code;
    }

    private void saveOtpToFile(String code, User user, Long operationId) {
        try {
            String logEntry = String.format("[%s] User: %s, OTP: %s, Operation: %d%n",
                    LocalDateTime.now(),
                    user.getUsername(),
                    code,
                    operationId);

            fileStorageService.appendToFile("otp_codes.log", logEntry);
            log.debug("OTP code saved to file for user: {}", user.getUsername());
        } catch (IOException e) {
            log.error("Failed to save OTP to file for user: {}", user.getUsername(), e);
        }
    }

    private void sendNotifications(String code, User user) {
        notificationSenders.forEach(sender -> {
            try {
                String message = String.format("Ваш код подтверждения: %s", code);
                sender.send(message, user);
                log.debug("Notification sent via {} to user: {}",
                        sender.getClass().getSimpleName(), user.getUsername());
            } catch (Exception e) {
                log.error("Failed to send notification via {} to user: {}",
                        sender.getClass().getSimpleName(), user.getUsername(), e);
            }
        });
    }

    @Transactional
    public void validateCode(ValidateCodeRequestDto requestDto) {
        User user = userService.getAuthorizedUser();
        log.info("Validating OTP code for user: {}", user.getUsername());

        OtpCode otpCode = getOtpCodeWithUser(requestDto.code(), user.getId());

        if (!otpCode.getStatus().equals(OtpStatus.ACTIVE)) {
            log.warn("Attempt to use inactive OTP code by user: {}", user.getUsername());
            throw new OtpCodeNotActiveException();
        }

        if (isExpiredOtpCode(otpCode)) {
            log.warn("Attempt to use expired OTP code by user: {}", user.getUsername());
            throw new OtpCodeExpiredException();
        }

        otpCode.setStatus(OtpStatus.USED);
        log.info("OTP code successfully validated for user: {}", user.getUsername());
    }

    @Transactional(readOnly = true)
    public OtpCode getOtpCodeWithUser(String code, Long userId) {
        return otpCodeRepository.findByCodeAndUserId(code, userId)
                .orElseThrow(() -> {
                    log.info("Otp code not found: code={}, userId={}", code, userId);
                    return new EntityNotFoundException("OtpCode not found");
                });
    }

    @Transactional(readOnly = true)
    public OtpCode getOtpCode(String code) {
        return otpCodeRepository.findByCode(code)
                .orElseThrow(() -> {
                    log.info("Otp code not found: code={}", code);
                    return new EntityNotFoundException("OtpCode not found");
                });
    }

    private String generateRandomCode(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int idx = secureRandom.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(idx));
        }
        return sb.toString();
    }

    private boolean isExpiredOtpCode(OtpCode otpCode) {
        return otpCode.getExpiresAt().isBefore(LocalDateTime.now());
    }

    @Scheduled(fixedDelayString = "${otp.check-expired-delay-ms}")
    @Transactional
    public void checkOtpCodes() {
        int expireActiveCodes = otpCodeRepository.expireActiveCodes(LocalDateTime.now());
        log.info("OTP codes expired: {}", expireActiveCodes);
    }

    @Transactional
    public void deleteOtpCodeByUserId(Long userId) {
        otpCodeRepository.deleteByUser_Id(userId);
    }
}