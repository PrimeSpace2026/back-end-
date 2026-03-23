package primespace.demo.controller;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    private final ConcurrentHashMap<String, Long> tokens = new ConcurrentHashMap<>();

    private static final long TOKEN_TTL = 24 * 60 * 60 * 1000; // 24h

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        if (adminEmail.equals(email) && adminPassword.equals(password)) {
            String token = UUID.randomUUID().toString();
            tokens.put(token, System.currentTimeMillis() + TOKEN_TTL);
            return ResponseEntity.ok(Map.of("token", token));
        }
        return ResponseEntity.status(401).body(Map.of("error", "Email ou mot de passe incorrect"));
    }

    @GetMapping("/verify")
    public ResponseEntity<Map<String, Object>> verify(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            Long expiry = tokens.get(token);
            if (expiry != null && expiry > System.currentTimeMillis()) {
                return ResponseEntity.ok(Map.of("valid", true));
            }
        }
        return ResponseEntity.status(401).body(Map.of("valid", false));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            tokens.remove(authHeader.substring(7));
        }
        return ResponseEntity.ok(Map.of("message", "Déconnexion réussie"));
    }
}
