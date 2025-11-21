package com.zooManager.zooManager.service;

import com.zooManager.zooManager.Employee;
import com.zooManager.zooManager.configuration.EmailChangeToken;
import com.zooManager.zooManager.configuration.EmailSender;
import com.zooManager.zooManager.repository.EmailChangeTokenRepository;
import com.zooManager.zooManager.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EmailChangeService {
    private final EmployeeRepository employeeRepository;
    private final EmailChangeTokenRepository emailChangeTokenRepository;
    private final EmailSender emailSender;

    public EmailChangeService(EmployeeRepository employeeRepository,
                              EmailChangeTokenRepository emailChangeTokenRepository,
                              EmailSender emailSender) {
        this.employeeRepository = employeeRepository;
        this.emailChangeTokenRepository = emailChangeTokenRepository;
        this.emailSender = emailSender;
    }

    public void requestEmailChange(Employee user,String newEmail){
        String tokenValue = UUID.randomUUID().toString();
        EmailChangeToken token = new EmailChangeToken();
        token.setToken(tokenValue);
        token.setUser(user);
        token.setNewEmail(newEmail);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        emailChangeTokenRepository.save(token);
        String link = "http://localhost:8081/settings/confirm-email-change?token=" + tokenValue;
        emailSender.send(newEmail, "Potwierdź zmiane adresu email",
                "Aby potwierdzić zmianę emaila kliknij w link:\n" + link);


    }
    public boolean confirmEmailChange(String tokenValue){
        EmailChangeToken token = emailChangeTokenRepository.findByToken(tokenValue)
                .orElse(null);
        if (token == null || token.getExpiresAt().isBefore(LocalDateTime.now())){
            return false;
        }
        Employee user = token.getUser();
        user.setEmail(token.getNewEmail());
        employeeRepository.save(user);
        emailChangeTokenRepository.delete(token);
        return true;
    }
}



