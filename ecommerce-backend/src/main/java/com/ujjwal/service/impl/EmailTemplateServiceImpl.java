package com.ujjwal.service.impl;

import com.ujjwal.modal.EmailTemplate;
import com.ujjwal.repository.EmailTemplateRepository;
import com.ujjwal.service.EmailService;
import com.ujjwal.service.EmailTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailTemplateServiceImpl implements EmailTemplateService {

    private final EmailTemplateRepository templateRepository;
    private final EmailService emailService;

    @Override
    public void sendDynamicEmail(String to, String templateCode, Map<String, Object> context) {
        EmailTemplate template = templateRepository.findByTemplateCode(templateCode)
                .orElseThrow(() -> new RuntimeException("Email template not found: " + templateCode));

        if (!template.getIsActive()) {
            log.warn("Email template {} is inactive, skipping email to {}", templateCode, to);
            return;
        }

        String subject = replacePlaceholders(template.getSubject(), context);
        String body = replacePlaceholders(template.getBody(), context);

        emailService.sendEmail(to, subject, body);
    }

    private String replacePlaceholders(String text, Map<String, Object> context) {
        if (text == null || context == null)
            return text;

        for (Map.Entry<String, Object> entry : context.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            text = text.replace(placeholder, value);
        }
        return text;
    }
}
