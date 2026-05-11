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

import primespace.demo.model.CustomTag;
import primespace.demo.repository.CustomTagRepository;

@RestController
@RequestMapping("/api/tours/{tourId}/custom-tags")
public class CustomTagController {

    private final CustomTagRepository customTagRepository;

    public CustomTagController(CustomTagRepository customTagRepository) {
        this.customTagRepository = customTagRepository;
    }

    @GetMapping
    public List<CustomTag> getCustomTags(@PathVariable Long tourId) {
        return customTagRepository.findByTourId(tourId);
    }

    @PostMapping
    public CustomTag createCustomTag(@PathVariable Long tourId, @RequestBody CustomTag tag) {
        tag.setTourId(tourId);
        if (tag.getEnabled() == null) tag.setEnabled(true);
        if (tag.getStemHeight() == null) tag.setStemHeight(0.3);
        if (tag.getColor() == null) tag.setColor("#4A90D9");
        return customTagRepository.save(tag);
    }

    @PutMapping("/{tagId}")
    public ResponseEntity<CustomTag> updateCustomTag(
            @PathVariable Long tourId,
            @PathVariable Long tagId,
            @RequestBody CustomTag details) {
        return customTagRepository.findById(tagId)
                .filter(tag -> tag.getTourId().equals(tourId))
                .map(tag -> {
                    tag.setLabel(details.getLabel());
                    tag.setDescription(details.getDescription());
                    tag.setMediaType(details.getMediaType());
                    tag.setMediaUrl(details.getMediaUrl());
                    tag.setIconUrl(details.getIconUrl());
                    tag.setIconName(details.getIconName());
                    tag.setColor(details.getColor());
                    tag.setAnchorX(details.getAnchorX());
                    tag.setAnchorY(details.getAnchorY());
                    tag.setAnchorZ(details.getAnchorZ());
                    tag.setStemHeight(details.getStemHeight());
                    tag.setStemDirX(details.getStemDirX());
                    tag.setStemDirY(details.getStemDirY());
                    tag.setStemDirZ(details.getStemDirZ());
                    tag.setFloorIndex(details.getFloorIndex());
                    tag.setEnabled(details.getEnabled());
                    return ResponseEntity.ok(customTagRepository.save(tag));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{tagId}")
    public ResponseEntity<Void> deleteCustomTag(@PathVariable Long tourId, @PathVariable Long tagId) {
        return customTagRepository.findById(tagId)
                .filter(tag -> tag.getTourId().equals(tourId))
                .map(tag -> {
                    customTagRepository.delete(tag);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
