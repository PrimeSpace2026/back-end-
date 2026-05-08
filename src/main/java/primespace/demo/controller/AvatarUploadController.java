package primespace.demo.controller;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/avatar-upload")
public class AvatarUploadController {

    @Value("${supabase.url:https://orpnrybtrnuqxfkrrnvx.supabase.co}")
    private String supabaseUrl;

    @Value("${supabase.key:}")
    private String supabaseKey;

    private static final String BUCKET = "tour-images";
    private static final String TEMP_DIR = "uploads/avatars/";
    private final HttpClient httpClient = HttpClient.newHttpClient();

    /**
     * Upload bytes to Supabase Storage and return the public URL.
     */
    private String uploadToSupabase(byte[] data, String fileName, String contentType) throws Exception {
        String uploadUrl = supabaseUrl + "/storage/v1/object/" + BUCKET + "/avatars/" + fileName;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uploadUrl))
                .header("Authorization", "Bearer " + supabaseKey)
                .header("Content-Type", contentType)
                .POST(HttpRequest.BodyPublishers.ofByteArray(data))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200 || response.statusCode() == 201) {
            return supabaseUrl + "/storage/v1/object/public/" + BUCKET + "/avatars/" + fileName;
        } else {
            throw new RuntimeException("Supabase upload failed (" + response.statusCode() + "): " + response.body());
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            String originalName = file.getOriginalFilename();
            if (originalName == null) originalName = "avatar";
            String lower = originalName.toLowerCase();
            String uid = UUID.randomUUID().toString().substring(0, 8);

            // Single image file → Supabase
            if (lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg")
                    || lower.endsWith(".gif") || lower.endsWith(".webp")) {
                String ext = lower.substring(lower.lastIndexOf('.'));
                String fileName = uid + ext;
                String contentType = file.getContentType() != null ? file.getContentType() : "image/" + ext.substring(1);
                String publicUrl = uploadToSupabase(file.getBytes(), fileName, contentType);
                return ResponseEntity.ok(Map.of("url", publicUrl));
            }

            // Single GLB file → convert to unlit materials, then upload to Supabase
            if (lower.endsWith(".glb")) {
                String fileName = uid + ".glb";
                // Save temp, convert to unlit, upload
                Path tempIn = Paths.get(TEMP_DIR, uid + "-in.glb");
                Path tempOut = Paths.get(TEMP_DIR, uid + "-unlit.glb");
                Files.createDirectories(tempIn.getParent());
                Files.write(tempIn, file.getBytes());
                try {
                    ProcessBuilder pb = new ProcessBuilder("gltf-transform", "unlit",
                            tempIn.toAbsolutePath().toString(), tempOut.toAbsolutePath().toString());
                    pb.redirectErrorStream(true);
                    Process proc = pb.start();
                    proc.waitFor();
                    byte[] data = Files.exists(tempOut) ? Files.readAllBytes(tempOut) : file.getBytes();
                    String publicUrl = uploadToSupabase(data, fileName, "model/gltf-binary");
                    return ResponseEntity.ok(Map.of("url", publicUrl));
                } finally {
                    Files.deleteIfExists(tempIn);
                    Files.deleteIfExists(tempOut);
                }
            }

            // Single GLTF file → Supabase
            if (lower.endsWith(".gltf")) {
                String fileName = uid + ".gltf";
                String publicUrl = uploadToSupabase(file.getBytes(), fileName, "model/gltf+json");
                return ResponseEntity.ok(Map.of("url", publicUrl));
            }

            // RAR/7Z - not supported
            if (lower.endsWith(".rar") || lower.endsWith(".7z")) {
                return ResponseEntity.status(400).body(Map.of("error",
                        "RAR/7Z not supported. Please compress as ZIP: Right-click folder → Send to → Compressed (zipped) folder"));
            }

            // ZIP → extract, convert GLTF to GLB, upload GLB to Supabase
            if (lower.endsWith(".zip")) {
                Path extractDir = Paths.get(TEMP_DIR, uid);
                Files.createDirectories(extractDir);

                try {
                    // Extract ZIP
                    try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(file.getBytes()))) {
                        ZipEntry entry;
                        while ((entry = zis.getNextEntry()) != null) {
                            if (entry.isDirectory()) {
                                Files.createDirectories(extractDir.resolve(entry.getName()));
                                continue;
                            }
                            Path entryPath = extractDir.resolve(entry.getName()).normalize();
                            if (!entryPath.startsWith(extractDir)) continue;
                            Files.createDirectories(entryPath.getParent());
                            Files.copy(zis, entryPath, StandardCopyOption.REPLACE_EXISTING);
                        }
                    }

                    // Find .glb first → convert to unlit, upload
                    Optional<Path> glb = Files.walk(extractDir)
                            .filter(p -> p.toString().toLowerCase().endsWith(".glb"))
                            .findFirst();
                    if (glb.isPresent()) {
                        Path unlitOut = extractDir.resolve("unlit.glb");
                        new ProcessBuilder("gltf-transform", "unlit",
                                glb.get().toAbsolutePath().toString(), unlitOut.toAbsolutePath().toString())
                                .redirectErrorStream(true).start().waitFor();
                        byte[] glbData = Files.exists(unlitOut) ? Files.readAllBytes(unlitOut) : Files.readAllBytes(glb.get());
                        String fileName = uid + ".glb";
                        String publicUrl = uploadToSupabase(glbData, fileName, "model/gltf-binary");
                        return ResponseEntity.ok(Map.of("url", publicUrl));
                    }

                    // Find .gltf → convert to GLB → convert to unlit → upload
                    Optional<Path> gltf = Files.walk(extractDir)
                            .filter(p -> p.toString().toLowerCase().endsWith(".gltf"))
                            .findFirst();
                    if (gltf.isPresent()) {
                        Path glbOut = extractDir.resolve("model.glb");
                        ProcessBuilder pb = new ProcessBuilder("gltf-pipeline", "-i",
                                gltf.get().toAbsolutePath().toString(), "-o", glbOut.toAbsolutePath().toString());
                        pb.redirectErrorStream(true);
                        Process proc = pb.start();
                        String output = new String(proc.getInputStream().readAllBytes());
                        int exitCode = proc.waitFor();

                        if (exitCode == 0 && Files.exists(glbOut)) {
                            // Convert to unlit
                            Path unlitOut = extractDir.resolve("unlit.glb");
                            new ProcessBuilder("gltf-transform", "unlit",
                                    glbOut.toAbsolutePath().toString(), unlitOut.toAbsolutePath().toString())
                                    .redirectErrorStream(true).start().waitFor();
                            byte[] glbData = Files.exists(unlitOut) ? Files.readAllBytes(unlitOut) : Files.readAllBytes(glbOut);
                            String fileName = uid + ".glb";
                            String publicUrl = uploadToSupabase(glbData, fileName, "model/gltf-binary");
                            return ResponseEntity.ok(Map.of("url", publicUrl));
                        } else {
                            return ResponseEntity.status(500).body(Map.of("error",
                                    "GLTF to GLB conversion failed: " + output));
                        }
                    }

                    return ResponseEntity.status(400).body(Map.of("error", "No GLB or GLTF file found in ZIP"));
                } finally {
                    // Clean up temp files
                    try {
                        Files.walk(extractDir)
                                .sorted(Comparator.reverseOrder())
                                .forEach(p -> { try { Files.deleteIfExists(p); } catch (Exception ignored) {} });
                    } catch (Exception ignored) {}
                }
            }

            return ResponseEntity.status(400).body(Map.of("error",
                    "Unsupported file type. Use PNG, GIF, GLB, or ZIP (with GLTF model)"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Upload failed: " + e.getMessage()));
        }
    }
}
