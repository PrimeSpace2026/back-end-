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

import primespace.demo.model.TourItem;
import primespace.demo.repository.TourItemRepository;

@RestController
@RequestMapping("/api/tours/{tourId}/items")
public class TourItemController {

    private final TourItemRepository tourItemRepository;

    public TourItemController(TourItemRepository tourItemRepository) {
        this.tourItemRepository = tourItemRepository;
    }

    @GetMapping
    public List<TourItem> getItems(@PathVariable Long tourId) {
        return tourItemRepository.findByTourId(tourId);
    }

    @PostMapping
    public TourItem createItem(@PathVariable Long tourId, @RequestBody TourItem item) {
        item.setTourId(tourId);
        return tourItemRepository.save(item);
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<TourItem> updateItem(@PathVariable Long tourId, @PathVariable Long itemId, @RequestBody TourItem details) {
        return tourItemRepository.findById(itemId)
                .filter(item -> item.getTourId().equals(tourId))
                .map(item -> {
                    item.setName(details.getName());
                    item.setDescription(details.getDescription());
                    item.setImageUrl(details.getImageUrl());
                    item.setPrice(details.getPrice());
                    item.setCurrency(details.getCurrency());
                    item.setExternalUrl(details.getExternalUrl());
                    item.setBrand(details.getBrand());
                    item.setShowAddToCart(details.getShowAddToCart());
                    item.setTagSid(details.getTagSid());
                    item.setCoordinates(details.getCoordinates());
                    return ResponseEntity.ok(tourItemRepository.save(item));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long tourId, @PathVariable Long itemId) {
        return tourItemRepository.findById(itemId)
                .filter(item -> item.getTourId().equals(tourId))
                .map(item -> {
                    tourItemRepository.delete(item);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
