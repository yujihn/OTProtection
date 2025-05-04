package com.mHide.otpService.repository;

import com.mHide.otpService.model.otp.OtpCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {
    Optional<OtpCode> findByCode(String code);

    Optional<OtpCode> findByCodeAndUserId(String code, Long userId);

    @Modifying
    @Query("UPDATE OtpCode o " +
            "SET o.status = 'EXPIRED' " +
            "WHERE o.status = 'ACTIVE' " +
            "  AND o.expiresAt < :now")
    int expireActiveCodes(@Param("now") LocalDateTime now);

    void deleteByUser_Id(Long userId);
}
