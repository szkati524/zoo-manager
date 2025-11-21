package com.zooManager.zooManager.service;

import com.zooManager.zooManager.Employee;
import com.zooManager.zooManager.configuration.EmailSender;
import com.zooManager.zooManager.configuration.PasswordResetToken;
import com.zooManager.zooManager.repository.EmployeeRepository;
import com.zooManager.zooManager.repository.PasswordResetTokenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordChangeService {
    private final EmployeeRepository employeeRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailSender emailSender;
    private final PasswordEncoder passwordEncoder;

    public PasswordChangeService(EmployeeRepository employeeRepository,
                                 PasswordResetTokenRepository passwordResetTokenRepository,
                                 EmailSender emailSender,
                                 PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailSender = emailSender;
        this.passwordEncoder = passwordEncoder;
    }
    public void requestPasswordChange(Employee user, String newPassword) {
        String tokenValue = UUID.randomUUID().toString();
        PasswordResetToken token = new PasswordResetToken();
        token.setToken(tokenValue);
        token.setUser(user);
        token.setNewPassword(passwordEncoder.encode(newPassword));
        token.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        passwordResetTokenRepository.save(token);
        String link = "http://localhost:8081/settings/confirm-password-change?token=" + tokenValue;
        emailSender.send(user.getEmail(),
                "Potwierdź zmianę hasła",
                "Aby potwierdzić zmianę hasła kliknij tutaj:\n" + link);
    }

    public boolean confirmPasswordChange(String tokenValue) {
        PasswordResetToken token = passwordResetTokenRepository.findByToken(tokenValue)
                .orElse(null);
        if (token == null || token.getExpiresAt().isBefore(LocalDateTime.now())) {
            return false;
        }
        Employee user = token.getUser();
        user.setPassword(token.getNewPassword());
        employeeRepository.save(user);
        passwordResetTokenRepository.delete(token);
        return true;
    }
}
