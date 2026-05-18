package primespace.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import primespace.demo.model.TourTag;

public interface TourTagRepository extends JpaRepository<TourTag, Long> {
    List<TourTag> findByTourId(Long tourId);

    @org.springframework.transaction.annotation.Transactional
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("DELETE FROM TourTag e WHERE e.tourId = :tourId")
    void deleteByTourId(@org.springframework.data.repository.query.Param("tourId") Long tourId);

    void deleteByNameStartingWith(String prefix);
}
