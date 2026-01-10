package com.zooManager.zooManager.service;

import com.zooManager.zooManager.Employee;
import com.zooManager.zooManager.configuration.EmailChangeToken;
import com.zooManager.zooManager.configuration.EmailSender;
import com.zooManager.zooManager.repository.EmailChangeTokenRepository;
import com.zooManager.zooManager.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailChangeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private EmailChangeTokenRepository emailChangeTokenRepository;
    @Mock
    private EmailSender emailSender;

    @InjectMocks
    private EmailChangeService emailChangeService;
    private Employee testUser;

    @BeforeEach
    void setUp(){
        testUser = new Employee();
        testUser.setEmail("test@o2.pl");
        testUser.setUsername("test123");
    }
    @Test
    void requestEmailChange_ShouldSaveTokenAndSendEmail(){
        emailChangeService.requestEmailChange(testUser,"new@o2.pl");
        verify(emailChangeTokenRepository,times(1)).save(any(EmailChangeToken.class));
        verify(emailSender,times(1)).send(eq("new@o2.pl")
        ,anyString()
        ,contains("http://localhost:8081/settings/confirm-email-change?token="));
    }
    @Test
    void confirmedEmailChange_Success_ShouldUpdateUserEmailAndDeleteToken(){
        String tokenValue = "valid-token";
        EmailChangeToken token = new EmailChangeToken();
        token.setToken(tokenValue);
        token.setNewEmail("new@o2.pl");
        token.setUser(testUser);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        when(emailChangeTokenRepository.findByToken(tokenValue)).thenReturn(Optional.of(token));
        boolean result = emailChangeService.confirmEmailChange(tokenValue);
        assertTrue(result);
        assertEquals("new@o2.pl",testUser.getEmail());
        verify(employeeRepository).save(testUser);
        verify(emailChangeTokenRepository).delete(token);
    }
    @Test
    void confirmEmailChange_ExpiredToken_ShouldReturnFalse() {

        String tokenValue = "expired-token";
        EmailChangeToken token = new EmailChangeToken();
        token.setToken(tokenValue);

        token.setExpiresAt(LocalDateTime.now().minusMinutes(1));

        when(emailChangeTokenRepository.findByToken(tokenValue)).thenReturn(Optional.of(token));

        // When
        boolean result = emailChangeService.confirmEmailChange(tokenValue);

        // Then
        assertFalse(result);
        verify(employeeRepository, never()).save(any());
        verify(emailChangeTokenRepository, never()).delete(any());
    }
}

