package com.mHide.otpService.service.notification;

import com.mHide.otpService.model.user.User;

public interface NotificationSender {
    void send(String message, User user);
}
