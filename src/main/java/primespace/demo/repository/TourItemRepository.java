package primespace.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import primespace.demo.model.TourItem;

public interface TourItemRepository extends JpaRepository<TourItem, Long> {
    List<TourItem> findByTourId(Long tourId);
}
