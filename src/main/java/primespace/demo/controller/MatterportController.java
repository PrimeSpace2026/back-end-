package primespace.demo.controller;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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

    private static final String GRAPH_URL = "https://my.matterport.com/api/mp/models/graph";

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

            // Use Matterport GraphQL API to get real mattertags
            String graphQuery = "{\"query\":\"{ model(id: \\\"" + modelId.replace("\"", "") +
                    "\\\") { id name mattertags { id label description color enabled } } }\"}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(GRAPH_URL))
                    .header("Content-Type", "application/json")
                    .header("User-Agent", "Mozilla/5.0")
                    .header("Origin", "https://my.matterport.com")
                    .POST(HttpRequest.BodyPublishers.ofString(graphQuery))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();

            List<Map<String, String>> tags = parseGraphResponse(body);

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("modelId", modelId);
            result.put("totalTags", tags.size());
            result.put("tags", tags);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch tags: " + e.getMessage()));
        }
    }

    /**
     * Parses mattertags from the GraphQL response using regex (no Jackson needed).
     * Expected format: "mattertags" : [ { "id":"...", "label":"...", ... }, ... ]
     */
    private List<Map<String, String>> parseGraphResponse(String json) {
        List<Map<String, String>> tags = new ArrayList<>();
        if (json == null || json.isEmpty()) return tags;

        // Find the mattertags array section
        int mattertagsIdx = json.indexOf("\"mattertags\"");
        if (mattertagsIdx < 0) return tags;

        // Find the array start
        int arrayStart = json.indexOf('[', mattertagsIdx);
        if (arrayStart < 0) return tags;

        // Find matching array end
        int arrayEnd = findMatchingBracket(json, arrayStart);
        if (arrayEnd < 0) return tags;

        String arrayContent = json.substring(arrayStart, arrayEnd + 1);

        // Extract individual tag objects
        Pattern idPattern = Pattern.compile("\"id\"\\s*:\\s*\"([^\"]+)\"");
        Pattern labelPattern = Pattern.compile("\"label\"\\s*:\\s*\"([^\"]*?)\"");
        Pattern descPattern = Pattern.compile("\"description\"\\s*:\\s*\"([^\"]*?)\"");
        Pattern colorPattern = Pattern.compile("\"color\"\\s*:\\s*\"([^\"]*?)\"");
        Pattern enabledPattern = Pattern.compile("\"enabled\"\\s*:\\s*(true|false)");

        // Split by object boundaries
        int searchFrom = 0;
        while (searchFrom < arrayContent.length()) {
            int objStart = arrayContent.indexOf('{', searchFrom);
            if (objStart < 0) break;
            int objEnd = findMatchingBrace(arrayContent, objStart);
            if (objEnd < 0) break;

            String obj = arrayContent.substring(objStart, objEnd + 1);

            Matcher idMatcher = idPattern.matcher(obj);
            if (idMatcher.find()) {
                String id = idMatcher.group(1);

                Matcher lm = labelPattern.matcher(obj);
                String label = lm.find() ? lm.group(1) : "";

                Matcher dm = descPattern.matcher(obj);
                String description = dm.find() ? dm.group(1) : "";

                Matcher cm = colorPattern.matcher(obj);
                String color = cm.find() ? cm.group(1) : "";

                Matcher em = enabledPattern.matcher(obj);
                boolean enabled = em.find() ? Boolean.parseBoolean(em.group(1)) : true;

                if (enabled) {
                    Map<String, String> tag = new LinkedHashMap<>();
                    tag.put("sid", id);
                    tag.put("label", label);
                    tag.put("description", description);
                    tag.put("color", color);
                    tags.add(tag);
                }
            }

            searchFrom = objEnd + 1;
        }

        return tags;
    }

    private int findMatchingBracket(String json, int openPos) {
        if (openPos < 0 || openPos >= json.length()) return -1;
        int depth = 0;
        boolean inString = false;
        boolean escaped = false;
        for (int i = openPos; i < json.length(); i++) {
            char c = json.charAt(i);
            if (escaped) { escaped = false; continue; }
            if (c == '\\') { escaped = true; continue; }
            if (c == '"') { inString = !inString; continue; }
            if (inString) continue;
            if (c == '[') depth++;
            if (c == ']') { depth--; if (depth == 0) return i; }
        }
        return -1;
    }

    private int findMatchingBrace(String json, int openPos) {
        if (openPos < 0 || openPos >= json.length()) return -1;
        int depth = 0;
        boolean inString = false;
        boolean escaped = false;
        for (int i = openPos; i < json.length(); i++) {
            char c = json.charAt(i);
            if (escaped) { escaped = false; continue; }
            if (c == '\\') { escaped = true; continue; }
            if (c == '"') { inString = !inString; continue; }
            if (inString) continue;
            if (c == '{') depth++;
            if (c == '}') { depth--; if (depth == 0) return i; }
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
