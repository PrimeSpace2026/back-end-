package primespace.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import primespace.demo.model.TourTag;

public interface TourTagRepository extends JpaRepository<TourTag, Long> {
    List<TourTag> findByTourId(Long tourId);
    void deleteByTourId(Long tourId);
}
