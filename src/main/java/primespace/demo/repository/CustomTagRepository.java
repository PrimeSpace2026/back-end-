package primespace.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import primespace.demo.model.CustomTag;

public interface CustomTagRepository extends JpaRepository<CustomTag, Long> {
    List<CustomTag> findByTourId(Long tourId);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("DELETE FROM CustomTag e WHERE e.tourId = :tourId")
    void deleteByTourId(@org.springframework.data.repository.query.Param("tourId") Long tourId);
}
