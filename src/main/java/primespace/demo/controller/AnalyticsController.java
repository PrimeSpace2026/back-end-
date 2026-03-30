package primespace.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import primespace.demo.model.Tour;
import primespace.demo.model.TourEvent;
import primespace.demo.model.TourVisit;
import primespace.demo.repository.TourEventRepository;
import primespace.demo.repository.TourRepository;
import primespace.demo.repository.TourVisitRepository;

import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final TourVisitRepository visitRepo;
    private final TourEventRepository eventRepo;
    private final TourRepository tourRepo;

    public AnalyticsController(TourVisitRepository visitRepo, TourEventRepository eventRepo, TourRepository tourRepo) {
        this.visitRepo = visitRepo;
        this.eventRepo = eventRepo;
        this.tourRepo = tourRepo;
    }

    // --- Track a visit start ---
    @PostMapping("/visit")
    public ResponseEntity<TourVisit> trackVisit(@RequestBody Map<String, Object> body) {
        TourVisit visit = new TourVisit();
        visit.setTourId(((Number) body.get("tourId")).longValue());
        visit.setVisitorId((String) body.getOrDefault("visitorId", "anonymous"));
        visit.setBrowser((String) body.getOrDefault("browser", ""));
        visit.setCountry((String) body.getOrDefault("country", ""));
        visit.setCity((String) body.getOrDefault("city", ""));
        if (body.get("latitude") != null) visit.setLatitude(((Number) body.get("latitude")).doubleValue());
        if (body.get("longitude") != null) visit.setLongitude(((Number) body.get("longitude")).doubleValue());
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

    // --- Global analytics dashboard ---
    @GetMapping("/global/stats")
    public ResponseEntity<Map<String, Object>> getGlobalStats() {
        Map<String, Object> stats = new LinkedHashMap<>();

        // Total visits & unique visitors
        long totalVisits = visitRepo.count();
        long uniqueVisitors = visitRepo.countUniqueVisitors();
        Double avgDuration = visitRepo.avgDurationGlobal();
        stats.put("totalVisits", totalVisits);
        stats.put("uniqueVisitors", uniqueVisitors);
        stats.put("avgDuration", avgDuration);

        // Global event counts
        stats.put("tagClicks", eventRepo.countByEventType("tag_click"));
        stats.put("productClicks", eventRepo.countByEventType("product_click"));
        stats.put("addToCart", eventRepo.countByEventType("add_to_cart"));

        // Most clicked product (global)
        List<Object[]> topProducts = eventRepo.countByTargetGroupedGlobal("product_click");
        if (!topProducts.isEmpty()) {
            Map<String, Object> top = new HashMap<>();
            top.put("name", topProducts.get(0)[0]);
            top.put("clicks", topProducts.get(0)[1]);
            stats.put("mostClickedProduct", top);
        } else {
            stats.put("mostClickedProduct", null);
        }

        // Most clicked tag (global)
        List<Object[]> topTags = eventRepo.countByTargetGroupedGlobal("tag_click");
        if (!topTags.isEmpty()) {
            Map<String, Object> top = new HashMap<>();
            top.put("name", topTags.get(0)[0]);
            top.put("clicks", topTags.get(0)[1]);
            stats.put("mostClickedTag", top);
        } else {
            stats.put("mostClickedTag", null);
        }

        // Product interaction heatmap (global)
        List<Map<String, Object>> productHeatmap = new ArrayList<>();
        for (Object[] row : topProducts) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("name", row[0]);
            entry.put("clicks", row[1]);
            productHeatmap.add(entry);
        }
        stats.put("productHeatmap", productHeatmap);

        // Tag interaction heatmap (global)
        List<Map<String, Object>> tagHeatmap = new ArrayList<>();
        for (Object[] row : topTags) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("name", row[0]);
            entry.put("clicks", row[1]);
            tagHeatmap.add(entry);
        }
        stats.put("tagHeatmap", tagHeatmap);

        // Per-tour breakdown (sorted by visits desc)
        Map<Long, String> tourNames = new HashMap<>();
        for (Tour t : tourRepo.findAll()) {
            tourNames.put(t.getId(), t.getName());
        }

        List<Object[]> visitsPerTour = visitRepo.countVisitsPerTour();
        List<Map<String, Object>> tourBreakdown = new ArrayList<>();
        for (Object[] row : visitsPerTour) {
            Long tourId = ((Number) row[0]).longValue();
            long count = ((Number) row[1]).longValue();
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("tourId", tourId);
            entry.put("tourName", tourNames.getOrDefault(tourId, "Tour #" + tourId));
            entry.put("visits", count);
            entry.put("avgDuration", visitRepo.avgDurationByTourId(tourId));
            entry.put("tagClicks", eventRepo.countByTourIdAndEventType(tourId, "tag_click"));
            entry.put("productClicks", eventRepo.countByTourIdAndEventType(tourId, "product_click"));
            entry.put("addToCart", eventRepo.countByTourIdAndEventType(tourId, "add_to_cart"));
            tourBreakdown.add(entry);
        }
        // Include tours with 0 visits
        for (Map.Entry<Long, String> e : tourNames.entrySet()) {
            boolean found = tourBreakdown.stream().anyMatch(m -> m.get("tourId").equals(e.getKey()));
            if (!found) {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("tourId", e.getKey());
                entry.put("tourName", e.getValue());
                entry.put("visits", 0L);
                entry.put("avgDuration", null);
                entry.put("tagClicks", 0L);
                entry.put("productClicks", 0L);
                entry.put("addToCart", 0L);
                tourBreakdown.add(entry);
            }
        }
        stats.put("tourBreakdown", tourBreakdown);

        // Browser breakdown (global)
        List<TourVisit> allVisits = visitRepo.findAllByOrderByStartedAtDesc();
        Map<String, Integer> browserCounts = new LinkedHashMap<>();
        for (TourVisit v : allVisits) {
            String b = v.getBrowser() != null && !v.getBrowser().isEmpty() ? v.getBrowser() : "Unknown";
            browserCounts.merge(b, 1, Integer::sum);
        }
        List<Map<String, Object>> browserList = new ArrayList<>();
        browserCounts.entrySet().stream()
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .forEach(e -> {
                Map<String, Object> m = new HashMap<>();
                m.put("name", e.getKey());
                m.put("count", e.getValue());
                browserList.add(m);
            });
        stats.put("browsers", browserList);

        // Location breakdown (global)
        Map<String, Integer> locationCounts = new LinkedHashMap<>();
        for (TourVisit v : allVisits) {
            String loc = "";
            if (v.getCity() != null && !v.getCity().isEmpty()) loc = v.getCity();
            if (v.getCountry() != null && !v.getCountry().isEmpty()) loc += (loc.isEmpty() ? "" : ", ") + v.getCountry();
            if (loc.isEmpty()) loc = "Unknown";
            locationCounts.merge(loc, 1, Integer::sum);
        }
        List<Map<String, Object>> locationList = new ArrayList<>();
        locationCounts.entrySet().stream()
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .forEach(e -> {
                Map<String, Object> m = new HashMap<>();
                m.put("name", e.getKey());
                m.put("count", e.getValue());
                locationList.add(m);
            });
        stats.put("locations", locationList);

        // Geographic map points (global)
        List<Map<String, Object>> mapPoints = new ArrayList<>();
        Map<String, double[]> coordMap = new LinkedHashMap<>();
        Map<String, Integer> coordCounts = new LinkedHashMap<>();
        for (TourVisit v : allVisits) {
            if (v.getLatitude() != null && v.getLongitude() != null) {
                String key = String.format("%.2f,%.2f", v.getLatitude(), v.getLongitude());
                coordMap.putIfAbsent(key, new double[]{v.getLatitude(), v.getLongitude()});
                coordCounts.merge(key, 1, Integer::sum);
            }
        }
        coordCounts.forEach((key, count) -> {
            double[] coords = coordMap.get(key);
            Map<String, Object> pt = new HashMap<>();
            pt.put("lat", coords[0]);
            pt.put("lng", coords[1]);
            pt.put("count", count);
            String loc = "";
            for (TourVisit v : allVisits) {
                if (v.getLatitude() != null && v.getLongitude() != null &&
                    String.format("%.2f,%.2f", v.getLatitude(), v.getLongitude()).equals(key)) {
                    if (v.getCity() != null && !v.getCity().isEmpty()) loc = v.getCity();
                    if (v.getCountry() != null && !v.getCountry().isEmpty()) loc += (loc.isEmpty() ? "" : ", ") + v.getCountry();
                    break;
                }
            }
            pt.put("name", loc.isEmpty() ? "Unknown" : loc);
            mapPoints.add(pt);
        });
        stats.put("mapPoints", mapPoints);

        // Recent visits (global, last 50)
        List<Map<String, Object>> recentVisitsList = new ArrayList<>();
        for (TourVisit v : allVisits.subList(0, Math.min(50, allVisits.size()))) {
            Map<String, Object> vMap = new LinkedHashMap<>();
            vMap.put("id", v.getId());
            vMap.put("tourId", v.getTourId());
            vMap.put("tourName", tourNames.getOrDefault(v.getTourId(), "Tour #" + v.getTourId()));
            vMap.put("visitorId", v.getVisitorId());
            vMap.put("startedAt", v.getStartedAt());
            vMap.put("durationSeconds", v.getDurationSeconds());
            vMap.put("browser", v.getBrowser());
            vMap.put("country", v.getCountry());
            vMap.put("city", v.getCity());
            recentVisitsList.add(vMap);
        }
        stats.put("recentVisits", recentVisitsList);

        return ResponseEntity.ok(stats);
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
            vMap.put("browser", v.getBrowser());
            vMap.put("country", v.getCountry());
            vMap.put("city", v.getCity());
            vMap.put("latitude", v.getLatitude());
            vMap.put("longitude", v.getLongitude());
            visitsList.add(vMap);
        }
        stats.put("recentVisits", visitsList);

        // Browser breakdown
        Map<String, Integer> browserCounts = new LinkedHashMap<>();
        for (TourVisit v : recentVisits) {
            String b = v.getBrowser() != null && !v.getBrowser().isEmpty() ? v.getBrowser() : "Inconnu";
            browserCounts.merge(b, 1, Integer::sum);
        }
        List<Map<String, Object>> browserList = new ArrayList<>();
        browserCounts.entrySet().stream()
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .forEach(e -> {
                Map<String, Object> m = new HashMap<>();
                m.put("name", e.getKey());
                m.put("count", e.getValue());
                browserList.add(m);
            });
        stats.put("browsers", browserList);

        // Location breakdown
        Map<String, Integer> locationCounts = new LinkedHashMap<>();
        for (TourVisit v : recentVisits) {
            String loc = "";
            if (v.getCity() != null && !v.getCity().isEmpty()) loc = v.getCity();
            if (v.getCountry() != null && !v.getCountry().isEmpty()) loc += (loc.isEmpty() ? "" : ", ") + v.getCountry();
            if (loc.isEmpty()) loc = "Inconnu";
            locationCounts.merge(loc, 1, Integer::sum);
        }
        List<Map<String, Object>> locationList = new ArrayList<>();
        locationCounts.entrySet().stream()
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .forEach(e -> {
                Map<String, Object> m = new HashMap<>();
                m.put("name", e.getKey());
                m.put("count", e.getValue());
                locationList.add(m);
            });
        stats.put("locations", locationList);

        // Map points for geographic heatmap
        List<Map<String, Object>> mapPoints = new ArrayList<>();
        Map<String, double[]> coordMap = new LinkedHashMap<>();
        Map<String, Integer> coordCounts = new LinkedHashMap<>();
        for (TourVisit v : recentVisits) {
            if (v.getLatitude() != null && v.getLongitude() != null) {
                String key = String.format("%.2f,%.2f", v.getLatitude(), v.getLongitude());
                coordMap.putIfAbsent(key, new double[]{v.getLatitude(), v.getLongitude()});
                coordCounts.merge(key, 1, Integer::sum);
            }
        }
        coordCounts.forEach((key, count) -> {
            double[] coords = coordMap.get(key);
            Map<String, Object> pt = new HashMap<>();
            pt.put("lat", coords[0]);
            pt.put("lng", coords[1]);
            pt.put("count", count);
            String loc = "";
            for (TourVisit v : recentVisits) {
                if (v.getLatitude() != null && v.getLongitude() != null &&
                    String.format("%.2f,%.2f", v.getLatitude(), v.getLongitude()).equals(key)) {
                    if (v.getCity() != null && !v.getCity().isEmpty()) loc = v.getCity();
                    if (v.getCountry() != null && !v.getCountry().isEmpty()) loc += (loc.isEmpty() ? "" : ", ") + v.getCountry();
                    break;
                }
            }
            pt.put("name", loc.isEmpty() ? "Inconnu" : loc);
            mapPoints.add(pt);
        });
        stats.put("mapPoints", mapPoints);

        return ResponseEntity.ok(stats);
    }
}
