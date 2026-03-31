package primespace.demo.controller;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/matterport")
public class MatterportController {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @GetMapping("/tags")
    public ResponseEntity<?> getTags(
            @RequestParam(required = false) String modelId,
            @RequestParam(required = false) String url) {
        try {
            if ((modelId == null || modelId.isEmpty()) && url != null && !url.isEmpty()) {
                modelId = extractModelId(url);
            }
            if (modelId == null || modelId.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Provide modelId or url parameter"));
            }

            // Try fetching model JSON from Matterport player API
            String apiUrl = "https://my.matterport.com/api/player/models/" + modelId + "/";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Accept", "application/json")
                    .header("User-Agent", "Mozilla/5.0")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();

            List<Map<String, String>> tags = parseTagsFromJson(body);

            if (tags.isEmpty()) {
                // Fallback: try the showcase page and scrape __INITIAL_DATA__
                tags = fetchFromShowcase(modelId);
            }

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("modelId", modelId);
            result.put("totalTags", tags.size());
            result.put("tags", tags);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch tags: " + e.getMessage()));
        }
    }

    private List<Map<String, String>> fetchFromShowcase(String modelId) {
        try {
            String showcaseUrl = "https://my.matterport.com/show/?m=" + modelId;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(showcaseUrl))
                    .header("User-Agent", "Mozilla/5.0")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return parseTagsFromJson(response.body());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * Parses mattertag entries from raw JSON text using regex.
     * Looks for patterns like "sid":"xxx" near "label":"yyy" in JSON arrays.
     */
    private List<Map<String, String>> parseTagsFromJson(String json) {
        List<Map<String, String>> tags = new ArrayList<>();
        if (json == null || json.isEmpty()) return tags;

        // Pattern to find mattertag-like objects with sid and label
        // Matches: {"sid":"value", ... "label":"value" ...}
        Pattern sidPattern = Pattern.compile("\"sid\"\\s*:\\s*\"([^\"]+)\"");
        Pattern labelPattern = Pattern.compile("\"label\"\\s*:\\s*\"([^\"]*?)\"");
        Pattern descPattern = Pattern.compile("\"description\"\\s*:\\s*\"([^\"]*?)\"");
        Pattern colorPattern = Pattern.compile("\"color\"\\s*:\\s*\"([^\"]*?)\"");

        // Find all JSON object blocks that contain a "sid" field
        // Split on potential object boundaries and check each chunk
        int searchFrom = 0;
        while (searchFrom < json.length()) {
            int sidIdx = json.indexOf("\"sid\"", searchFrom);
            if (sidIdx < 0) break;

            // Get surrounding context (the JSON object containing this sid)
            int blockStart = json.lastIndexOf('{', sidIdx);
            int blockEnd = findMatchingBrace(json, blockStart);
            if (blockStart < 0 || blockEnd < 0) {
                searchFrom = sidIdx + 5;
                continue;
            }

            String block = json.substring(blockStart, Math.min(blockEnd + 1, json.length()));

            Matcher sidMatcher = sidPattern.matcher(block);
            if (sidMatcher.find()) {
                String sid = sidMatcher.group(1);

                // Skip non-mattertag sids (like model sids, floor sids etc)
                // Mattertag sids typically contain alphanumeric + hyphens
                if (sid.isEmpty()) {
                    searchFrom = sidIdx + 5;
                    continue;
                }

                Map<String, String> tag = new LinkedHashMap<>();
                tag.put("sid", sid);

                Matcher lm = labelPattern.matcher(block);
                tag.put("label", lm.find() ? lm.group(1) : "");

                Matcher dm = descPattern.matcher(block);
                tag.put("description", dm.find() ? dm.group(1) : "");

                Matcher cm = colorPattern.matcher(block);
                tag.put("color", cm.find() ? cm.group(1) : "");

                // Avoid duplicates
                boolean duplicate = false;
                for (Map<String, String> existing : tags) {
                    if (existing.get("sid").equals(sid)) {
                        duplicate = true;
                        break;
                    }
                }
                if (!duplicate) {
                    tags.add(tag);
                }
            }

            searchFrom = blockEnd > sidIdx ? blockEnd : sidIdx + 5;
        }

        return tags;
    }

    private int findMatchingBrace(String json, int openPos) {
        if (openPos < 0 || openPos >= json.length()) return -1;
        int depth = 0;
        boolean inString = false;
        boolean escaped = false;
        for (int i = openPos; i < json.length() && i < openPos + 5000; i++) {
            char c = json.charAt(i);
            if (escaped) {
                escaped = false;
                continue;
            }
            if (c == '\\') {
                escaped = true;
                continue;
            }
            if (c == '"') {
                inString = !inString;
                continue;
            }
            if (inString) continue;
            if (c == '{') depth++;
            if (c == '}') {
                depth--;
                if (depth == 0) return i;
            }
        }
        return -1;
    }

    private String extractModelId(String url) {
        if (url.contains("m=")) {
            String after = url.substring(url.indexOf("m=") + 2);
            int end = after.indexOf('&');
            return end > 0 ? after.substring(0, end) : after;
        }
        if (url.contains("/models/")) {
            String after = url.substring(url.indexOf("/models/") + 8);
            int end = after.indexOf('/');
            if (end < 0) end = after.indexOf('?');
            return end > 0 ? after.substring(0, end) : after;
        }
        return url.trim();
    }
}
