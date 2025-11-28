package com.revcart.service.impl;

import com.revcart.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.logging.Logger;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {

    private static final Logger log = Logger.getLogger(MailServiceImpl.class.getName());

    private final JavaMailSender mailSender;

    public MailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendOtp(String email, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("RevCart OTP Verification");
            helper.setText("Your OTP code is: " + otp, false);
            mailSender.send(message);
        } catch (MessagingException | MailException ex) {
            log.warning("Failed to send email, falling back to log-only mode: " + ex.getMessage());
            log.info("OTP for " + email + " is " + otp);
        }
    }
}

