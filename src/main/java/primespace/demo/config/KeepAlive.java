package primespace.demo.config;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class KeepAlive {

    private static final Logger log = LoggerFactory.getLogger(KeepAlive.class);

    @Value("${RENDER_EXTERNAL_URL:}")
    private String renderUrl;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Scheduled(fixedRate = 300_000) // every 5 minutes
    public void ping() {
        if (renderUrl == null || renderUrl.isBlank()) {
            return;
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(renderUrl + "/api/wake"))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            log.debug("Keep-alive ping: {}", response.statusCode());
        } catch (Exception e) {
            log.warn("Keep-alive ping failed: {}", e.getMessage());
        }
    }
}
