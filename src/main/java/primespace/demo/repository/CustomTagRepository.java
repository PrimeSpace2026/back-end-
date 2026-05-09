package primespace.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import primespace.demo.model.CustomTag;

public interface CustomTagRepository extends JpaRepository<CustomTag, Long> {
    List<CustomTag> findByTourId(Long tourId);
    void deleteByTourId(Long tourId);
}
