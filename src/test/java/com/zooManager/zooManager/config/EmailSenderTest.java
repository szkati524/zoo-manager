package com.zooManager.zooManager.config;

import com.zooManager.zooManager.configuration.EmailSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;



@ExtendWith(MockitoExtension.class)
public class EmailSenderTest {
    @Mock
    private MockMvc mockMvc;
    @Mock
    private JavaMailSender javaMailSender;
    @InjectMocks
    private EmailSender emailSender;
    @Test
    void send_ShouldPopulateMessageCorrectlyAndCallJavaMailSender(){
        String to = "user@example.com";
        String subject = "Temat";
        String content = "Treść wiadomości";
        emailSender.send(to,subject,content);
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertEquals(to,sentMessage.getTo()[0]);
        assertEquals(subject,sentMessage.getSubject());
        assertEquals(content,sentMessage.getText());
        assertEquals("zookeeper2@wp.pl",sentMessage.getFrom());
    }
}
