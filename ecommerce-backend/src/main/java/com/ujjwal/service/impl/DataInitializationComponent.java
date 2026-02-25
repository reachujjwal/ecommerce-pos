package com.ujjwal.service.impl;

import com.ujjwal.domain.UserRole;
import com.ujjwal.modal.EmailTemplate;
import com.ujjwal.modal.User;
import com.ujjwal.repository.EmailTemplateRepository;
import com.ujjwal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializationComponent implements CommandLineRunner {

    private final UserRepository userRepository;
    private final EmailTemplateRepository emailTemplateRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initializeAdminUser();
        initializeEmailTemplates();
    }

    private void initializeAdminUser() {
        String adminUsername = "ujjwal.admin@gmail.com";

        if (userRepository.findByEmail(adminUsername) == null) {
            User adminUser = new User();

            adminUser.setPassword(passwordEncoder.encode("ujjwal.admin@123"));
            adminUser.setFullName("Ujjwal");
            adminUser.setEmail(adminUsername);
            adminUser.setRole(UserRole.ROLE_ADMIN);

            User admin = userRepository.save(adminUser);
        }
    }

    private void initializeEmailTemplates() {
        if (emailTemplateRepository.findByTemplateCode("ORDER_PLACEMENT").isEmpty()) {
            emailTemplateRepository.save(EmailTemplate.builder()
                    .templateCode("ORDER_PLACEMENT")
                    .subject("Order Placed Successfully")
                    .body("<h1>Hello {{customerName}}!</h1>" +
                            "<p>Your order #{{orderId}} has been placed successfully.</p>" +
                            "<p><strong>Total Amount:</strong> ₹{{totalAmount}}</p>" +
                            "<p><strong>Delivery:</strong> {{deliveryType}}</p>" +
                            "<h3>Order Details:</h3>" +
                            "{{itemsTable}}" +
                            "<p>Thank you for shopping with us!</p>")
                    .build());
        }

        if (emailTemplateRepository.findByTemplateCode("ACCOUNT_CREATION").isEmpty()) {
            emailTemplateRepository.save(EmailTemplate.builder()
                    .templateCode("ACCOUNT_CREATION")
                    .subject("Welcome to our Store!")
                    .body("<h1>Welcome {{fullName}}!</h1>" +
                            "<p>Your account has been created successfully with email: {{email}}.</p>" +
                            "<p>We're glad to have you with us.</p>")
                    .build());
        }

        if (emailTemplateRepository.findByTemplateCode("FORGOT_PASSWORD").isEmpty()) {
            emailTemplateRepository.save(EmailTemplate.builder()
                    .templateCode("FORGOT_PASSWORD")
                    .subject("Password Reset Request")
                    .body("<h1>Hi {{fullName}},</h1>" +
                            "<p>You requested to reset your password. Use the link below (valid for {{expiryMinutes}} minutes):</p>"
                            +
                            "<p><a href='{{resetLink}}'>Reset Password</a></p>" +
                            "<p>If you didn't request this, please ignore this email.</p>")
                    .build());
        }
    }
}
