package primespace.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import primespace.demo.model.FurnitureModel;

public interface FurnitureModelRepository extends JpaRepository<FurnitureModel, Long> {
    List<FurnitureModel> findByCategory(String category);
}
