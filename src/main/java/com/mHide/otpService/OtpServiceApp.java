package com.mHide.otpService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableConfigurationProperties
public class OtpServiceApp {

    public static void main(String[] args) {

        SpringApplication.run(OtpServiceApp.class, args);
    }
}
