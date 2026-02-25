package com.ujjwal.service;

import java.util.Map;

public interface EmailTemplateService {
    void sendDynamicEmail(String to, String templateCode, Map<String, Object> context);
}
