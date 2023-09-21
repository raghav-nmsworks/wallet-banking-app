package com.nextuple.walletbankingapp.service;

import com.nextuple.walletbankingapp.service.EmailSenderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.*;

public class EmailSenderServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailSenderService emailSenderService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSendSimpleEmail() {
        String toEmail = "recipient@example.com";
        String body = "Test email body";
        String subject = "Test email subject";

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailSenderService.sendSimpleEmail(toEmail, body, subject);
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}
