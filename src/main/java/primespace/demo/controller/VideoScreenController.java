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

import primespace.demo.model.VideoScreen;
import primespace.demo.repository.VideoScreenRepository;

@RestController
@RequestMapping("/api/tours/{tourId}/video-screens")
public class VideoScreenController {

    private final VideoScreenRepository videoScreenRepository;

    public VideoScreenController(VideoScreenRepository videoScreenRepository) {
        this.videoScreenRepository = videoScreenRepository;
    }

    @GetMapping
    public List<VideoScreen> getVideoScreens(@PathVariable Long tourId) {
        return videoScreenRepository.findByTourId(tourId);
    }

    @PostMapping
    public VideoScreen createVideoScreen(@PathVariable Long tourId, @RequestBody VideoScreen screen) {
        screen.setId(null);
        screen.setTourId(tourId);
        return videoScreenRepository.save(screen);
    }

    @PutMapping("/{screenId}")
    public ResponseEntity<VideoScreen> updateVideoScreen(@PathVariable Long tourId, @PathVariable Long screenId,
                                                          @RequestBody VideoScreen details) {
        return videoScreenRepository.findById(screenId)
                .filter(s -> s.getTourId().equals(tourId))
                .map(s -> {
                    s.setName(details.getName());
                    s.setYoutubeUrl(details.getYoutubeUrl());
                    s.setPosX(details.getPosX());
                    s.setPosY(details.getPosY());
                    s.setPosZ(details.getPosZ());
                    s.setRotX(details.getRotX());
                    s.setRotY(details.getRotY());
                    s.setRotZ(details.getRotZ());
                    s.setWidth(details.getWidth());
                    s.setHeight(details.getHeight());
                    s.setIconType(details.getIconType());
                    s.setVisibilityRange(details.getVisibilityRange());
                    return ResponseEntity.ok(videoScreenRepository.save(s));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{screenId}")
    public ResponseEntity<Void> deleteVideoScreen(@PathVariable Long tourId, @PathVariable Long screenId) {
        return videoScreenRepository.findById(screenId)
                .filter(s -> s.getTourId().equals(tourId))
                .map(s -> {
                    videoScreenRepository.delete(s);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
