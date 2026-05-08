package primespace.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import primespace.demo.model.TourGuide;

public interface TourGuideRepository extends JpaRepository<TourGuide, Long> {
    List<TourGuide> findByTourId(Long tourId);
}
