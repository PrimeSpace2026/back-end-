package primespace.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import primespace.demo.model.VideoScreen;

public interface VideoScreenRepository extends JpaRepository<VideoScreen, Long> {
    List<VideoScreen> findByTourId(Long tourId);
        @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("DELETE FROM VideoScreen e WHERE e.tourId = :tourId")
    void deleteAllByTourId(@org.springframework.data.repository.query.Param("tourId") Long tourId);
}
