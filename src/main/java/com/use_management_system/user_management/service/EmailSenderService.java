package com.use_management_system.user_management.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {

    private static final Logger log = LoggerFactory.getLogger(EmailSenderService.class);

    private final JavaMailSender mailSender;
    private final String fromEmail;

    public EmailSenderService(JavaMailSender mailSender,
                              @Value("${app.email.from:no-reply@example.com}") String fromEmail) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
    }

    public void sendVerificationEmail(String toEmail, String username, String verificationUrl) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Verify your email address");
            message.setText(buildVerificationBody(username, verificationUrl));

            mailSender.send(message);
        } catch (Exception ex) {
            log.error("Failed to send verification email to {}", toEmail, ex);
        }
    }

    private String buildVerificationBody(String username, String verificationUrl) {
        return "Hi " + username + ",\\n\\n"
                + "Please verify your email by clicking the link below:\\n"
                + verificationUrl + "\\n\\n"
                + "This link expires in 15 minutes.\\n\\n"
                + "If you did not create this account, you can ignore this email.";
    }
}
