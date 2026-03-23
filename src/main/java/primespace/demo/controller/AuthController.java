package primespace.demo.controller;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import primespace.demo.model.User;
import primespace.demo.repository.UserRepository;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final ConcurrentHashMap<String, Long> tokens = new ConcurrentHashMap<>();
    private static final long TOKEN_TTL = 24 * 60 * 60 * 1000; // 24h
    private static final long RESET_TOKEN_TTL = 60 * 60 * 1000; // 1h

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        String name = body.get("name");

        if (email == null || password == null || name == null ||
            email.isBlank() || password.isBlank() || name.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Tous les champs sont obligatoires"));
        }

        if (password.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("error", "Le mot de passe doit contenir au moins 6 caractères"));
        }

        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Cet email est déjà utilisé"));
        }

        User user = new User(email, hashPassword(password), name);
        userRepository.save(user);

        String token = UUID.randomUUID().toString();
        tokens.put(token, System.currentTimeMillis() + TOKEN_TTL);

        return ResponseEntity.ok(Map.of("token", token, "message", "Compte créé avec succès"));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent() && checkPassword(password, userOpt.get().getPassword())) {
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

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        Optional<User> userOpt = userRepository.findByEmail(email);

        // Always return success to prevent email enumeration
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String resetToken = UUID.randomUUID().toString();
            user.setResetToken(resetToken);
            user.setResetTokenExpiry(System.currentTimeMillis() + RESET_TOKEN_TTL);
            userRepository.save(user);
        }

        return ResponseEntity.ok(Map.of(
            "message", "Si cet email existe, un code de réinitialisation a été généré."
        ));
    }

    @PostMapping("/verify-reset-token")
    public ResponseEntity<Map<String, Object>> verifyResetToken(@RequestBody Map<String, String> body) {
        String resetToken = body.get("token");
        Optional<User> userOpt = userRepository.findByResetToken(resetToken);

        if (userOpt.isPresent() && userOpt.get().getResetTokenExpiry() > System.currentTimeMillis()) {
            return ResponseEntity.ok(Map.of("valid", true));
        }
        return ResponseEntity.badRequest().body(Map.of("valid", false, "error", "Token invalide ou expiré"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> body) {
        String resetToken = body.get("token");
        String newPassword = body.get("password");

        if (newPassword == null || newPassword.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("error", "Le mot de passe doit contenir au moins 6 caractères"));
        }

        Optional<User> userOpt = userRepository.findByResetToken(resetToken);
        if (userOpt.isPresent() && userOpt.get().getResetTokenExpiry() > System.currentTimeMillis()) {
            User user = userOpt.get();
            user.setPassword(hashPassword(newPassword));
            user.setResetToken(null);
            user.setResetTokenExpiry(null);
            userRepository.save(user);
            return ResponseEntity.ok(Map.of("message", "Mot de passe réinitialisé avec succès"));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Token invalide ou expiré"));
    }

    // Simple salted SHA-256 hashing (no extra dependencies needed)
    private String hashPassword(String password) {
        try {
            byte[] salt = new byte[16];
            new SecureRandom().nextBytes(salt);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean checkPassword(String rawPassword, String storedHash) {
        try {
            String[] parts = storedHash.split(":");
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[1]);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] actualHash = md.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            return MessageDigest.isEqual(expectedHash, actualHash);
        } catch (Exception e) {
            return false;
        }
    }
}
