package primespace.demo.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import primespace.demo.model.Tour;
import primespace.demo.repository.TourRepository;

@RestController
@RequestMapping("/api/tours")
public class TourController {

    private static final Logger log = LoggerFactory.getLogger(TourController.class);

    private final TourRepository tourRepository;

    public TourController(TourRepository tourRepository) {
        this.tourRepository = tourRepository;
    }

    @GetMapping
    public ResponseEntity<?> getAllTours() {
        try {
            return ResponseEntity.ok(tourRepository.findAll());
        } catch (Exception e) {
            log.error("Failed to fetch tours", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getClass().getSimpleName(), "message", e.getMessage() != null ? e.getMessage() : "unknown"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tour> getTourById(@PathVariable Long id) {
        return tourRepository.findById(id)
                .filter(t -> Boolean.TRUE.equals(t.getEnabled()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Tour createTour(@RequestBody Tour tour) {
        return tourRepository.save(tour);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTour(@PathVariable Long id, @RequestBody Tour tourDetails) {
        try {
            return tourRepository.findById(id)
                    .map(tour -> {
                        if (tourDetails.getName() != null) tour.setName(tourDetails.getName());
                        if (tourDetails.getDescription() != null) tour.setDescription(tourDetails.getDescription());
                        if (tourDetails.getCategory() != null) tour.setCategory(tourDetails.getCategory());
                        if (tourDetails.getImageUrl() != null) tour.setImageUrl(tourDetails.getImageUrl());
                        if (tourDetails.getSurface() != null) tour.setSurface(tourDetails.getSurface());
                        if (tourDetails.getTourUrl() != null) tour.setTourUrl(tourDetails.getTourUrl());
                        if (tourDetails.getLatitude() != null) tour.setLatitude(tourDetails.getLatitude());
                        if (tourDetails.getLongitude() != null) tour.setLongitude(tourDetails.getLongitude());
                        if (tourDetails.getLocation() != null) tour.setLocation(tourDetails.getLocation());
                        if (tourDetails.getMetadataJson() != null) tour.setMetadataJson(tourDetails.getMetadataJson());
                        if (tourDetails.getEnabled() != null) tour.setEnabled(tourDetails.getEnabled());
                        return ResponseEntity.ok((Object) tourRepository.save(tour));
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("updateTour failed for id={}", id, e);
            Throwable root = e;
            while (root.getCause() != null) root = root.getCause();
            return ResponseEntity.status(500).body(Map.of(
                    "error", e.getClass().getSimpleName(),
                    "message", String.valueOf(e.getMessage()),
                    "rootCause", root.getClass().getSimpleName() + ": " + root.getMessage()
            ));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTour(@PathVariable Long id) {
        return tourRepository.findById(id)
                .map(tour -> {
                    tourRepository.delete(tour);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/enabled")
    public ResponseEntity<?> setEnabled(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        Boolean enabled = body.get("enabled");
        if (enabled == null) return ResponseEntity.badRequest().body(Map.of("error", "missing 'enabled' field"));
        return tourRepository.findById(id)
                .map(tour -> {
                    tour.setEnabled(enabled);
                    return ResponseEntity.ok((Object) tourRepository.save(tour));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
