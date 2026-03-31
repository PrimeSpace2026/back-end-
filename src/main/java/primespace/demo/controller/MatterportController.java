package primespace.demo.controller;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/matterport")
public class MatterportController {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Fetches all Matterport tags (mattertags) from a model, server-side.
     * No SDK key / domain restriction since this runs on the backend.
     *
     * Usage: GET /api/matterport/tags?modelId=rpt1CXMfYuj
     *    or: GET /api/matterport/tags?url=https://my.matterport.com/show/?m=rpt1CXMfYuj
     */
    @GetMapping("/tags")
    public ResponseEntity<?> getTags(
            @RequestParam(required = false) String modelId,
            @RequestParam(required = false) String url) {
        try {
            // Extract modelId from URL if provided
            if ((modelId == null || modelId.isEmpty()) && url != null && !url.isEmpty()) {
                modelId = extractModelId(url);
            }
            if (modelId == null || modelId.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Provide modelId or url parameter"));
            }

            // Fetch model details from Matterport's internal API
            String apiUrl = "https://my.matterport.com/api/player/models/" + modelId + "/";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Accept", "application/json")
                    .header("User-Agent", "Mozilla/5.0")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                // Try alternative endpoint
                return fetchTagsAlternative(modelId);
            }

            JsonNode root = objectMapper.readTree(response.body());
            List<Map<String, Object>> tags = extractTags(root);

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("modelId", modelId);
            result.put("totalTags", tags.size());
            result.put("tags", tags);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch tags: " + e.getMessage()));
        }
    }

    private ResponseEntity<?> fetchTagsAlternative(String modelId) {
        try {
            // Alternative: Matterport Graph API endpoint
            String graphUrl = "https://my.matterport.com/api/mp/models/graph"
                    + "?operationName=ModelMattertags"
                    + "&variable=" + URLEncoder.encode("{\"modelId\":\"" + modelId + "\"}", StandardCharsets.UTF_8)
                    + "&query="
                    + URLEncoder.encode(
                            "query ModelMattertags($modelId: ID!) { model(id: $modelId) { mattertags { sid label description anchorPosition { x y z } stemVector { x y z } color } } }",
                            StandardCharsets.UTF_8);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(graphUrl))
                    .header("Accept", "application/json")
                    .header("User-Agent", "Mozilla/5.0")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                // Fallback: try fetching the showcase page and extract model data
                return fetchTagsFromShowcase(modelId);
            }

            JsonNode root = objectMapper.readTree(response.body());
            JsonNode mattertags = root.path("data").path("model").path("mattertags");

            List<Map<String, Object>> tags = new ArrayList<>();
            if (mattertags.isArray()) {
                for (JsonNode tag : mattertags) {
                    Map<String, Object> t = new LinkedHashMap<>();
                    t.put("sid", tag.path("sid").asText(""));
                    t.put("label", tag.path("label").asText(""));
                    t.put("description", tag.path("description").asText(""));
                    t.put("color", tag.path("color").asText(""));
                    tags.add(t);
                }
            }

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("modelId", modelId);
            result.put("totalTags", tags.size());
            result.put("tags", tags);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Alternative fetch failed: " + e.getMessage()));
        }
    }

    private ResponseEntity<?> fetchTagsFromShowcase(String modelId) {
        try {
            // Fetch the showcase bundle JSON
            String bundleUrl = "https://my.matterport.com/api/player/models/" + modelId + "/files?type=3";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(bundleUrl))
                    .header("Accept", "application/json")
                    .header("User-Agent", "Mozilla/5.0")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode root = objectMapper.readTree(response.body());
            List<Map<String, Object>> tags = extractTags(root);

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("modelId", modelId);
            result.put("totalTags", tags.size());
            result.put("tags", tags);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Showcase fetch failed: " + e.getMessage()));
        }
    }

    private List<Map<String, Object>> extractTags(JsonNode root) {
        List<Map<String, Object>> tags = new ArrayList<>();

        // Try "mattertags" array
        JsonNode mattertags = root.path("mattertags");
        if (!mattertags.isMissingNode() && mattertags.isArray()) {
            for (JsonNode tag : mattertags) {
                tags.add(parseTag(tag));
            }
            return tags;
        }

        // Try nested "model.mattertags"
        mattertags = root.path("model").path("mattertags");
        if (!mattertags.isMissingNode() && mattertags.isArray()) {
            for (JsonNode tag : mattertags) {
                tags.add(parseTag(tag));
            }
            return tags;
        }

        // Try "tags" array
        JsonNode tagsNode = root.path("tags");
        if (!tagsNode.isMissingNode() && tagsNode.isArray()) {
            for (JsonNode tag : tagsNode) {
                tags.add(parseTag(tag));
            }
            return tags;
        }

        // Recursively search for any array containing "sid" fields
        findTagArrays(root, tags);

        return tags;
    }

    private void findTagArrays(JsonNode node, List<Map<String, Object>> tags) {
        if (node.isArray()) {
            for (JsonNode child : node) {
                if (child.isObject() && child.has("sid")) {
                    tags.add(parseTag(child));
                }
            }
        } else if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                findTagArrays(fields.next().getValue(), tags);
            }
        }
    }

    private Map<String, Object> parseTag(JsonNode tag) {
        Map<String, Object> t = new LinkedHashMap<>();
        t.put("sid", tag.path("sid").asText(tag.path("id").asText("")));
        t.put("label", tag.path("label").asText(tag.path("name").asText("")));
        t.put("description", tag.path("description").asText(""));
        if (tag.has("color")) {
            t.put("color", tag.path("color").asText(""));
        }
        if (tag.has("anchorPosition")) {
            JsonNode pos = tag.path("anchorPosition");
            t.put("position", Map.of(
                    "x", pos.path("x").asDouble(),
                    "y", pos.path("y").asDouble(),
                    "z", pos.path("z").asDouble()));
        }
        return t;
    }

    private String extractModelId(String url) {
        // Extract model ID from various Matterport URL formats
        // https://my.matterport.com/show/?m=rpt1CXMfYuj
        // https://my.matterport.com/show/?m=rpt1CXMfYuj&other=params
        if (url.contains("m=")) {
            String after = url.substring(url.indexOf("m=") + 2);
            int end = after.indexOf('&');
            return end > 0 ? after.substring(0, end) : after;
        }
        // https://my.matterport.com/models/rpt1CXMfYuj
        if (url.contains("/models/")) {
            String after = url.substring(url.indexOf("/models/") + 8);
            int end = after.indexOf('/');
            if (end < 0) end = after.indexOf('?');
            return end > 0 ? after.substring(0, end) : after;
        }
        return url.trim();
    }
}
