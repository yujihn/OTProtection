package com.mHide.otpService.repository;

import com.mHide.otpService.model.otp.OtpConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpConfigRepository extends JpaRepository<OtpConfig, Integer> {
    Optional<OtpConfig> findOtpConfigById(Integer id);
}