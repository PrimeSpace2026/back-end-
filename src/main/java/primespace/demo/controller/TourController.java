package primespace.demo.controller;

import java.util.List;

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

    private final TourRepository tourRepository;

    public TourController(TourRepository tourRepository) {
        this.tourRepository = tourRepository;
    }

    @GetMapping
    public List<Tour> getAllTours() {
        return tourRepository.findAll();
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
