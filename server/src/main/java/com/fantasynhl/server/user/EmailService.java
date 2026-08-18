package com.fantasynhl.server.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class EmailService {

    private final RestClient restClient;

    @Value("${emailjs.service-id}")
    private String serviceId;

    @Value("${emailjs.username-template-id}")
    private String usernameTemplateId;

    @Value("${emailjs.password-reset-template-id}")
    private String passwordResetTemplateId;

    @Value("${emailjs.public-key}")
    private String publicKey;

    @Value("${emailjs.private-key}")
    private String privateKey;

    public EmailService() {
        this.restClient = RestClient.create();
    }

    public void sendUsernameEmail(String toEmail, String username) {

        Map<String, Object> request = Map.of(
                "service_id", serviceId,
                "template_id", usernameTemplateId,
                "user_id", publicKey,
                "accessToken", privateKey,
                "template_params", Map.of(
                        "to_email", toEmail,
                        "username", username
                )
        );

        sendEmail(request);
    }

    public void sendPasswordResetEmail(String toEmail, String resetLink) {

        Map<String, Object> request = Map.of(
                "service_id", serviceId,
                "template_id", passwordResetTemplateId,
                "user_id", publicKey,
                "accessToken", privateKey,
                "template_params", Map.of(
                        "to_email", toEmail,
                        "reset_link", resetLink
                )
        );

        sendEmail(request);
    }

    private void sendEmail(Map<String, Object> request) {

        restClient.post()
                .uri("https://api.emailjs.com/api/v1.0/email/send")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}