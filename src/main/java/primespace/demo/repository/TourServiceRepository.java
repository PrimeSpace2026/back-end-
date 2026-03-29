package primespace.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import primespace.demo.model.TourService;

public interface TourServiceRepository extends JpaRepository<TourService, Long> {
    List<TourService> findByTourId(Long tourId);
}
