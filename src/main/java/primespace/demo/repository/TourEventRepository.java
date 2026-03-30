package primespace.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import primespace.demo.model.TourEvent;
import java.util.List;

public interface TourEventRepository extends JpaRepository<TourEvent, Long> {
    List<TourEvent> findByTourIdOrderByCreatedAtDesc(Long tourId);
    long countByTourIdAndEventType(Long tourId, String eventType);

    @Query("SELECT e.targetName, COUNT(e) FROM TourEvent e WHERE e.tourId = :tourId AND e.eventType = :eventType GROUP BY e.targetName ORDER BY COUNT(e) DESC")
    List<Object[]> countByTargetGrouped(Long tourId, String eventType);

    // Global queries
    long countByEventType(String eventType);

    @Query("SELECT e.targetName, COUNT(e) FROM TourEvent e WHERE e.eventType = :eventType GROUP BY e.targetName ORDER BY COUNT(e) DESC")
    List<Object[]> countByTargetGroupedGlobal(String eventType);

    @Query("SELECT e.tourId, COUNT(e) FROM TourEvent e WHERE e.eventType = :eventType GROUP BY e.tourId ORDER BY COUNT(e) DESC")
    List<Object[]> countByTourGrouped(String eventType);

    @Query("SELECT COUNT(DISTINCT e.visitorId) FROM TourEvent e WHERE e.tourId = :tourId AND e.eventType = :eventType")
    long countUniqueVisitorsByTourIdAndEventType(Long tourId, String eventType);

    @Query("SELECT COUNT(DISTINCT e.visitorId) FROM TourEvent e WHERE e.eventType = :eventType")
    long countUniqueVisitorsByEventType(String eventType);
}
