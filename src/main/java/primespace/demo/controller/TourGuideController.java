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

import primespace.demo.model.TourGuide;
import primespace.demo.repository.TourGuideRepository;

@RestController
@RequestMapping("/api/tours/{tourId}/guides")
public class TourGuideController {

    private final TourGuideRepository tourGuideRepository;

    public TourGuideController(TourGuideRepository tourGuideRepository) {
        this.tourGuideRepository = tourGuideRepository;
    }

    @GetMapping
    public List<TourGuide> getGuides(@PathVariable Long tourId) {
        return tourGuideRepository.findByTourId(tourId);
    }

    @PostMapping
    public TourGuide createGuide(@PathVariable Long tourId, @RequestBody TourGuide guide) {
        guide.setTourId(tourId);
        if (guide.getEnabled() == null) guide.setEnabled(true);
        if (guide.getLanguage() == null) guide.setLanguage("en");
        if (guide.getAvatarUrl() == null || guide.getAvatarUrl().isBlank())
            guide.setAvatarUrl("https://orpnrybtrnuqxfkrrnvx.supabase.co/storage/v1/object/public/tour-images/avatars/waitress-unlit.glb");
        return tourGuideRepository.save(guide);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TourGuide> updateGuide(@PathVariable Long tourId, @PathVariable Long id, @RequestBody TourGuide guide) {
        return tourGuideRepository.findById(id)
            .map(existing -> {
                if (guide.getName() != null) existing.setName(guide.getName());
                if (guide.getMessage() != null) existing.setMessage(guide.getMessage());
                if (guide.getLanguage() != null) existing.setLanguage(guide.getLanguage());
                if (guide.getPosX() != null) existing.setPosX(guide.getPosX());
                if (guide.getPosY() != null) existing.setPosY(guide.getPosY());
                if (guide.getPosZ() != null) existing.setPosZ(guide.getPosZ());
                if (guide.getRotY() != null) existing.setRotY(guide.getRotY());
                if (guide.getEnabled() != null) existing.setEnabled(guide.getEnabled());
                if (guide.getAvatarUrl() != null) existing.setAvatarUrl(guide.getAvatarUrl());
                if (guide.getPosition() != null) existing.setPosition(guide.getPosition());
                return ResponseEntity.ok(tourGuideRepository.save(existing));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGuide(@PathVariable Long tourId, @PathVariable Long id) {
        tourGuideRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
