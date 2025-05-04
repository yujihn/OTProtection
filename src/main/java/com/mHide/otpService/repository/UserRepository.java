package com.mHide.otpService.repository;

import com.mHide.otpService.model.user.User;
import com.mHide.otpService.model.user.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    List<User> findByRole(UserRole role);
}
