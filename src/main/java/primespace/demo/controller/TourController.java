package primespace.demo.controller;

import java.util.List;
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
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Tour createTour(@RequestBody Tour tour) {
        return tourRepository.save(tour);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tour> updateTour(@PathVariable Long id, @RequestBody Tour tourDetails) {
        return tourRepository.findById(id)
                .map(tour -> {
                    tour.setName(tourDetails.getName());
                    tour.setDescription(tourDetails.getDescription());
                    tour.setCategory(tourDetails.getCategory());
                    tour.setImageUrl(tourDetails.getImageUrl());
                    tour.setSurface(tourDetails.getSurface());
                    tour.setTourUrl(tourDetails.getTourUrl());
                    tour.setLatitude(tourDetails.getLatitude());
                    tour.setLongitude(tourDetails.getLongitude());
                    tour.setLocation(tourDetails.getLocation());
                    tour.setMetadataJson(tourDetails.getMetadataJson());
                    return ResponseEntity.ok(tourRepository.save(tour));
                })
                .orElse(ResponseEntity.notFound().build());
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
}
