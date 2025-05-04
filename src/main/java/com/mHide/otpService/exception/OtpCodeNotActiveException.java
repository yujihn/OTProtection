package com.mHide.otpService.exception;

public class OtpCodeNotActiveException extends RuntimeException{
    public OtpCodeNotActiveException() {
        super("OTP code not active.");
    }
}
