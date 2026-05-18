package primespace.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import primespace.demo.model.TourGuide;

public interface TourGuideRepository extends JpaRepository<TourGuide, Long> {
    List<TourGuide> findByTourId(Long tourId);
        @org.springframework.transaction.annotation.Transactional
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("DELETE FROM TourGuide e WHERE e.tourId = :tourId")
    void deleteAllByTourId(@org.springframework.data.repository.query.Param("tourId") Long tourId);
}
