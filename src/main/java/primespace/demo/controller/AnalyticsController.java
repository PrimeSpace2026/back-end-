package primespace.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import primespace.demo.model.TourEvent;
import primespace.demo.model.TourVisit;
import primespace.demo.repository.TourEventRepository;
import primespace.demo.repository.TourVisitRepository;

import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final TourVisitRepository visitRepo;
    private final TourEventRepository eventRepo;

    public AnalyticsController(TourVisitRepository visitRepo, TourEventRepository eventRepo) {
        this.visitRepo = visitRepo;
        this.eventRepo = eventRepo;
    }

    // --- Track a visit start ---
    @PostMapping("/visit")
    public ResponseEntity<TourVisit> trackVisit(@RequestBody Map<String, Object> body) {
        TourVisit visit = new TourVisit();
        visit.setTourId(((Number) body.get("tourId")).longValue());
        visit.setVisitorId((String) body.getOrDefault("visitorId", "anonymous"));
        visit.setStartedAt(Instant.now());
        return ResponseEntity.ok(visitRepo.save(visit));
    }

    // --- Update visit duration (POST for sendBeacon compatibility, PUT also accepted) ---
    @RequestMapping(value = "/visit/{id}", method = {RequestMethod.PUT, RequestMethod.POST})
    public ResponseEntity<TourVisit> updateVisitDuration(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        return visitRepo.findById(id).map(visit -> {
            visit.setDurationSeconds(((Number) body.get("durationSeconds")).intValue());
            return ResponseEntity.ok(visitRepo.save(visit));
        }).orElse(ResponseEntity.notFound().build());
    }

    // --- Track an event (tag_click, product_click, add_to_cart) ---
    @PostMapping("/event")
    public ResponseEntity<TourEvent> trackEvent(@RequestBody Map<String, Object> body) {
        TourEvent event = new TourEvent();
        event.setTourId(((Number) body.get("tourId")).longValue());
        event.setVisitorId((String) body.getOrDefault("visitorId", "anonymous"));
        event.setEventType((String) body.get("eventType"));
        event.setTargetName((String) body.getOrDefault("targetName", ""));
        event.setTargetId((String) body.getOrDefault("targetId", ""));
        event.setCreatedAt(Instant.now());
        return ResponseEntity.ok(eventRepo.save(event));
    }

    // --- Get stats for a tour ---
    @GetMapping("/tours/{tourId}/stats")
    public ResponseEntity<Map<String, Object>> getTourStats(@PathVariable Long tourId) {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalVisits", visitRepo.countByTourId(tourId));
        stats.put("avgDuration", visitRepo.avgDurationByTourId(tourId));
        stats.put("tagClicks", eventRepo.countByTourIdAndEventType(tourId, "tag_click"));
        stats.put("productClicks", eventRepo.countByTourIdAndEventType(tourId, "product_click"));
        stats.put("addToCart", eventRepo.countByTourIdAndEventType(tourId, "add_to_cart"));

        // Heatmap: clicks per product/tag
        List<Object[]> tagHeat = eventRepo.countByTargetGrouped(tourId, "tag_click");
        List<Map<String, Object>> heatmap = new ArrayList<>();
        for (Object[] row : tagHeat) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("name", row[0]);
            entry.put("clicks", row[1]);
            heatmap.add(entry);
        }
        stats.put("tagHeatmap", heatmap);

        List<Object[]> productHeat = eventRepo.countByTargetGrouped(tourId, "product_click");
        List<Map<String, Object>> productHeatmap = new ArrayList<>();
        for (Object[] row : productHeat) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("name", row[0]);
            entry.put("clicks", row[1]);
            productHeatmap.add(entry);
        }
        stats.put("productHeatmap", productHeatmap);

        // Recent visits with duration
        List<TourVisit> recentVisits = visitRepo.findByTourIdOrderByStartedAtDesc(tourId);
        List<Map<String, Object>> visitsList = new ArrayList<>();
        for (TourVisit v : recentVisits.subList(0, Math.min(50, recentVisits.size()))) {
            Map<String, Object> vMap = new HashMap<>();
            vMap.put("id", v.getId());
            vMap.put("visitorId", v.getVisitorId());
            vMap.put("startedAt", v.getStartedAt());
            vMap.put("durationSeconds", v.getDurationSeconds());
            visitsList.add(vMap);
        }
        stats.put("recentVisits", visitsList);

        return ResponseEntity.ok(stats);
    }
}
