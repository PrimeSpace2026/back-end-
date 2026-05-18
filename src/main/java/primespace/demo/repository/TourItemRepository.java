package primespace.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import primespace.demo.model.TourItem;

public interface TourItemRepository extends JpaRepository<TourItem, Long> {
    List<TourItem> findByTourId(Long tourId);
        @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("DELETE FROM TourItem e WHERE e.tourId = :tourId")
    void deleteAllByTourId(@org.springframework.data.repository.query.Param("tourId") Long tourId);
}
