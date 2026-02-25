package com.ujjwal.service.impl;

import com.ujjwal.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Override
    public void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setFrom("noreply@ecommerce-pos.com"); // Set from address
            helper.setSubject(subject);
            helper.setText(body, true);
            helper.setTo(to);

            System.out.println("📧 Sending Email to: " + to);
            System.out.println("Subject: " + subject);
            System.out.println("Body: \n" + body);
            System.out.println("-----------------------------------");

            javaMailSender.send(mimeMessage);
            System.out.println("✅ Email sent successfully to: " + to);
        } catch (MailException e) {
            System.err.println("❌ Failed to send email to: " + to);
            System.err.println("Error: " + e.getMessage());
            throw new MailSendException("Failed to send email: " + e.getMessage());
        } catch (MessagingException e) {
            System.err.println("❌ Failed to create email message for: " + to);
            System.err.println("Error: " + e.getMessage());
            throw new MailSendException("Failed to send email: " + e.getMessage());
        }
    }

}
