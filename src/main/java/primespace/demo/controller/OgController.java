package primespace.demo.controller;

import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import primespace.demo.model.Tour;
import primespace.demo.repository.TourRepository;

@RestController
public class OgController {

    private final TourRepository tourRepository;
    private static final String SITE_URL = "https://primespace.studio";

    public OgController(TourRepository tourRepository) {
        this.tourRepository = tourRepository;
    }

    @GetMapping(value = "/og/view/{id}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> tourOg(@PathVariable Long id, HttpServletRequest request) {
        Optional<Tour> opt = tourRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Tour tour = opt.get();
        String title = escapeHtml(tour.getName() != null ? tour.getName() : "PrimeSpace Tour");
        String description = escapeHtml(tour.getDescription() != null ? tour.getDescription()
                : "Explorez cette visite virtuelle 3D sur PrimeSpace");
        String image = tour.getImageUrl() != null ? tour.getImageUrl() : SITE_URL + "/logo.jpg";
        String qs = request.getQueryString();
        String redirectUrl = SITE_URL + "/view/" + id + (qs != null && !qs.isEmpty() ? "?" + qs : "");
        String canonicalUrl = SITE_URL + "/view/" + id;

        String html = """
                <!DOCTYPE html>
                <html lang="fr">
                <head>
                  <meta charset="UTF-8"/>
                  <title>%s | PrimeSpace</title>
                  <meta name="description" content="%s"/>
                  <meta property="og:title" content="%s"/>
                  <meta property="og:description" content="%s"/>
                  <meta property="og:image" content="%s"/>
                  <meta property="og:url" content="%s"/>
                  <meta property="og:type" content="website"/>
                  <meta property="og:site_name" content="PrimeSpace"/>
                  <meta name="twitter:card" content="summary_large_image"/>
                  <meta name="twitter:title" content="%s"/>
                  <meta name="twitter:description" content="%s"/>
                  <meta name="twitter:image" content="%s"/>
                  <meta http-equiv="refresh" content="0;url=%s"/>
                </head>
                <body>
                  <p>Redirecting to <a href="%s">%s</a>...</p>
                </body>
                </html>
                """.formatted(title, description, title, description, image, canonicalUrl,
                title, description, image, redirectUrl, redirectUrl, title);

        return ResponseEntity.ok(html);
    }

    private static String escapeHtml(String s) {
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
