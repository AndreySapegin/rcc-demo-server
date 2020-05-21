package app.business.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import app.business.entities.Model;

public interface ModelRepository extends JpaRepository<Model, String> {
	
	@Query("SELECT m FROM  #{#entityName} m LEFT JOIN Car c ON c.model = m.idModel GROUP BY m HAVING COUNT(c) = 0")
	List<Model> findAllModelsWithoutCars();

}
