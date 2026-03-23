package primespace.demo.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private final JavaMailSender mailSender;

    @Value("${contact.recipient:info@primespace.studio}")
    private String recipientEmail;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    public ContactController(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> handleContact(@RequestBody ContactRequest request) {
        if (request.name == null || request.name.isBlank() ||
            request.email == null || request.email.isBlank() ||
            request.projectType == null || request.projectType.isBlank() ||
            request.message == null || request.message.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Tous les champs obligatoires doivent être remplis"));
        }

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mail = new MimeMessageHelper(mimeMessage, "UTF-8");
            mail.setFrom(new InternetAddress(fromEmail, "PrimeSpace Studio"));
            mail.setTo(recipientEmail);
            mail.setReplyTo(request.email);
            mail.setSubject("Nouvelle demande de devis - " + request.name);
            mail.setText(buildEmailBody(request));

            mailSender.send(mimeMessage);

            return ResponseEntity.ok(Map.of("message", "Message envoyé avec succès"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erreur lors de l'envoi: " + e.getMessage()));
        }
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
