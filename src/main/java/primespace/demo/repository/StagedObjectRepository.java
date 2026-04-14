package primespace.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import primespace.demo.model.StagedObject;

public interface StagedObjectRepository extends JpaRepository<StagedObject, Long> {
    List<StagedObject> findByTourId(Long tourId);
}
