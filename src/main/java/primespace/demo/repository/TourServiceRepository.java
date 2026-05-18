package primespace.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import primespace.demo.model.TourService;

public interface TourServiceRepository extends JpaRepository<TourService, Long> {
    List<TourService> findByTourId(Long tourId);
        @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("DELETE FROM TourService e WHERE e.tourId = :tourId")
    void deleteAllByTourId(@org.springframework.data.repository.query.Param("tourId") Long tourId);
}
