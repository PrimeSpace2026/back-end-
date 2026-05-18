package primespace.demo.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import primespace.demo.model.Chamber;
import primespace.demo.repository.ChamberRepository;

@RestController
@RequestMapping("/api/tours/{tourId}/chambers")
public class ChamberController {

    private final ChamberRepository chamberRepository;

    public ChamberController(ChamberRepository chamberRepository) {
        this.chamberRepository = chamberRepository;
    }

    @GetMapping
    public List<Chamber> getChambers(@PathVariable Long tourId) {
        return chamberRepository.findByTourId(tourId);
    }

    @PostMapping
    public Chamber createChamber(@PathVariable Long tourId, @RequestBody Chamber chamber) {
        chamber.setTourId(tourId);
        return chamberRepository.save(chamber);
    }

    @PutMapping("/{chamberId}")
    public ResponseEntity<Chamber> updateChamber(@PathVariable Long tourId, @PathVariable Long chamberId,
                                                  @RequestBody Chamber details) {
        return chamberRepository.findById(chamberId)
                .filter(ch -> ch.getTourId().equals(tourId))
                .map(ch -> {
                    ch.setName(details.getName());
                    ch.setDescription(details.getDescription());
                    ch.setImageUrl(details.getImageUrl());
                    ch.setPrice(details.getPrice());
                    ch.setCurrency(details.getCurrency());
                    ch.setTagSid(details.getTagSid());
                    ch.setCoordinates(details.getCoordinates());
                    return ResponseEntity.ok(chamberRepository.save(ch));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{chamberId}")
    public ResponseEntity<Void> deleteChamber(@PathVariable Long tourId, @PathVariable Long chamberId) {
        return chamberRepository.findById(chamberId)
                .filter(ch -> ch.getTourId().equals(tourId))
                .map(ch -> {
                    chamberRepository.delete(ch);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/deduplicate")
    public ResponseEntity<String> deduplicateChambers(@PathVariable Long tourId) {
        List<Chamber> all = chamberRepository.findByTourId(tourId);
        Set<String> seen = new HashSet<>();
        List<Chamber> toDelete = new ArrayList<>();
        for (Chamber ch : all) {
            String key = (ch.getName() != null ? ch.getName() : "") + "|" + (ch.getTagSid() != null ? ch.getTagSid() : "");
            if (seen.contains(key)) {
                toDelete.add(ch);
            } else {
                seen.add(key);
            }
        }
        chamberRepository.deleteAll(toDelete);
        return ResponseEntity.ok("Removed " + toDelete.size() + " duplicates");
    }
}
