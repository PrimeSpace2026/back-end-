package primespace.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import primespace.demo.model.TourTag;
import primespace.demo.repository.TourTagRepository;

@RestController
@RequestMapping("/api/tours/{tourId}/tags")
public class TourTagController {

    private final TourTagRepository tourTagRepository;

    public TourTagController(TourTagRepository tourTagRepository) {
        this.tourTagRepository = tourTagRepository;
    }

    @GetMapping
    public List<TourTag> getTags(@PathVariable Long tourId) {
        return tourTagRepository.findByTourId(tourId);
    }

    // Bulk sync: replace all tags for a tour
    @PostMapping
    @Transactional
    public ResponseEntity<List<TourTag>> syncTags(@PathVariable Long tourId, @RequestBody List<TourTag> tags) {
        tourTagRepository.deleteByTourId(tourId);
        for (TourTag tag : tags) {
            tag.setId(null);
            tag.setTourId(tourId);
        }
        List<TourTag> saved = tourTagRepository.saveAll(tags);
        return ResponseEntity.ok(saved);
    }

    // Cleanup: remove old sweep tags (360° Vue <uuid>) from all tours
    @DeleteMapping("/cleanup-sweeps")
    @Transactional
    public ResponseEntity<String> cleanupSweepTags(@PathVariable Long tourId) {
        List<TourTag> all = tourTagRepository.findByTourId(tourId);
        List<TourTag> sweepTags = all.stream().filter(t -> t.getName() != null && t.getName().startsWith("360°")).toList();
        tourTagRepository.deleteAll(sweepTags);
        return ResponseEntity.ok("Removed " + sweepTags.size() + " sweep tags from tour " + tourId);
    }
}
