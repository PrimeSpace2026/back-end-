package primespace.demo.controller;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    @Value("${resend.api-key:}")
    private String resendApiKey;

    @Value("${contact.recipient:info@primespace.studio}")
    private String recipientEmail;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @PostMapping
    public ResponseEntity<Map<String, String>> handleContact(@RequestBody ContactRequest request) {
        if (request.name == null || request.name.isBlank() ||
            request.email == null || request.email.isBlank() ||
            request.projectType == null || request.projectType.isBlank() ||
            request.message == null || request.message.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Tous les champs obligatoires doivent être remplis"));
        }

        try {
            String emailBody = buildEmailBody(request);
            String subject = "Nouvelle demande de devis - " + request.name;

            String json = String.format(
                "{\"from\":\"%s via PrimeSpace <contact@primespace.studio>\","
                + "\"to\":[\"%s\"],"
                + "\"reply_to\":\"%s\","
                + "\"subject\":\"%s\","
                + "\"text\":\"%s\"}",
                escapeJson(request.name),
                escapeJson(recipientEmail),
                escapeJson(request.email),
                escapeJson(subject),
                escapeJson(emailBody)
            );

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.resend.com/emails"))
                    .header("Authorization", "Bearer " + resendApiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                return ResponseEntity.ok(Map.of("message", "Message envoyé avec succès"));
            } else {
                return ResponseEntity.status(500).body(Map.of("error", "Erreur lors de l'envoi: " + response.body()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erreur lors de l'envoi: " + e.getMessage()));
        }
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
    }

    private String buildEmailBody(ContactRequest r) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Nouvelle Demande de Devis ===\n\n");
        sb.append("Nom:            ").append(r.name).append("\n");
        sb.append("Email:          ").append(r.email).append("\n");
        sb.append("Téléphone:      ").append(r.phone != null ? r.phone : "Non fourni").append("\n");
        sb.append("Type de Projet: ").append(r.projectType).append("\n");
        sb.append("\n--- Message ---\n\n");
        sb.append(r.message).append("\n");
        sb.append("\n================================\n");
        sb.append("Envoyé depuis primespace.studio\n");
        return sb.toString();
    }

    static class ContactRequest {
        public String name;
        public String email;
        public String phone;
        public String projectType;
        public String message;
    }
}
