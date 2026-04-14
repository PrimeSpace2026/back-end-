package primespace.demo.controller;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/matterport")
public class MatterportController {

    private static final String GRAPH_URL = "https://my.matterport.com/api/models/graph";
    private static final String APP_KEY = "b7uar4u57xdec0zw7dwygt7md";
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @GetMapping("/tags")
    public ResponseEntity<String> getTags(@RequestParam String modelId) {
        String query = """
            {
              model(id: "%s") {
                mattertags {
                  sid
                  label
                  description
                  anchorPosition { x y z }
                }
              }
            }
            """.formatted(modelId);

        return proxyGraphQL(query);
    }

    @GetMapping("/sweeps")
    public ResponseEntity<String> getSweeps(@RequestParam String modelId) {
        String query = """
            {
              model(id: "%s") {
                sweeps {
                  id
                  uuid
                  position { x y z }
                  floor
                  neighbors
                }
              }
            }
            """.formatted(modelId);

        return proxyGraphQL(query);
    }

    private ResponseEntity<String> proxyGraphQL(String query) {
        try {
            String body = "{\"query\":" + escapeJson(query) + "}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(GRAPH_URL))
                    .header("Content-Type", "application/json")
                    .header("x-matterport-application-key", APP_KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .timeout(Duration.ofSeconds(15))
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            return ResponseEntity
                    .status(response.statusCode())
                    .header("Content-Type", "application/json")
                    .body(response.body());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("{\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}");
        }
    }

    private String escapeJson(String s) {
        return "\"" + s.replace("\\", "\\\\")
                       .replace("\"", "\\\"")
                       .replace("\n", "\\n")
                       .replace("\r", "")
                       .replace("\t", "\\t") + "\"";
    }
}
