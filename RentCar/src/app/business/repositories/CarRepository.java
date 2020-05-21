package app.business.repositories;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import app.business.entities.Car;
import app.business.entities.Model;

public interface CarRepository extends JpaRepository<Car, String> {

	
	List<Car> findAllByInUse(boolean use);

	List<Car> findAllByIsWritenOff(boolean b);

	List<Car> findAllByInUseAndModelIn(boolean b, List<String> listId);

	List<Car> findAllByInUseAndModelIdModel(boolean b, String idModel);
	
	@Query("SELECT c FROM #{#entityName} c")
	Stream<Car> findAllStream();
	
	@Query("SELECT m FROM #{#entityName} c JOIN c.model m WHERE c.inUse = false GROUP BY c.model")
	List<Model> findAllAvilableByModel();

	List<Car> findAllByModel(String modelId);

}
