package primespace.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import primespace.demo.model.TourVisit;
import java.util.List;

public interface TourVisitRepository extends JpaRepository<TourVisit, Long> {
    List<TourVisit> findByTourIdOrderByStartedAtDesc(Long tourId);
    long countByTourId(Long tourId);

    @Query("SELECT AVG(v.durationSeconds) FROM TourVisit v WHERE v.tourId = :tourId AND v.durationSeconds IS NOT NULL")
    Double avgDurationByTourId(Long tourId);
}
