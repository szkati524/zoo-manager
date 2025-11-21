package com.zooManager.zooManager.service;

import com.zooManager.zooManager.configuration.EmailSender;
import com.zooManager.zooManager.repository.EmployeeRepository;
import com.zooManager.zooManager.repository.PasswordResetTokenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {
    private final EmployeeRepository employeeRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailSender emailSender;
    private final PasswordEncoder passwordEncoder;

    private final EmailChangeService emailChangeService;
    private final PasswordChangeService passwordChangeService;

    public SettingsService(EmployeeRepository employeeRepository, PasswordResetTokenRepository passwordResetTokenRepository, EmailSender emailSender, PasswordEncoder passwordEncoder, EmailChangeService emailChangeService, PasswordChangeService passwordChangeService) {
        this.employeeRepository = employeeRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailSender = emailSender;
        this.passwordEncoder = passwordEncoder;
        this.emailChangeService = emailChangeService;
        this.passwordChangeService = passwordChangeService;
    }



}
