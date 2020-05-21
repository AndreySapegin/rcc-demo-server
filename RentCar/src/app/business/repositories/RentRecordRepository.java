package app.business.repositories;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import app.business.entities.Car;
import app.business.entities.Driver;
import app.business.entities.Model;
import app.business.entities.RentRecord;


public interface RentRecordRepository extends JpaRepository<RentRecord, Integer> {

	List<RentRecord> findAllByReceived(boolean received);

	List<RentRecord> findAllByRecordClosed(boolean close);

	@Query("SELECT r FROM #{#entityName} r WHERE r.driver.idDriver = ?1 AND COALESCE(r.returnDate, CAST(?2 as date)) > r.rentDate + r.rentDurationDays")
	List<RentRecord> findAllDelayedByDriverOnDate(String driverId, LocalDate onDate);

	List<RentRecord> findAllByDriverIdDriverAndRentDateBetween(String driverId, LocalDate beginRage, LocalDate endRange);
	List<RentRecord> findAllByDriverIdDriver(String driverId);
	
	List<RentRecord> findAllByRentDateBetween(LocalDate beign, LocalDate end);

	@Query("SELECT r FROM #{#entityName} r WHERE  r.rentDate BETWEEN ?1 AND ?2 AND  (r.rentDate + r.rentDurationDays) < COALESCE(r.returnDate, CAST(?2 as date))")
	List<RentRecord> findAllDelayedAndRentedInDateRange(LocalDate begin, LocalDate end);

	List<RentRecord> findAllByCarModelIdModelAndRentDateBetween(String modelId, LocalDate begin, LocalDate end);

	@Query("SELECT COALESCE(SUM(r.total),0) FROM #{#entityName} r WHERE r.recordClosed = true AND (r.rentDate BETWEEN ?2 AND ?3) AND r.car.model.idModel = ?1")
	double findTotalByModelInRange(String modelId, LocalDate begin, LocalDate end);

	@Query("SELECT m AS model, COUNT(r) AS rentCount, SUM(r.total)-SUM(r.damageRepairPrice) As sumSale FROM #{#entityName} r JOIN r.car.model m GROUP BY r.car.model")
	Stream<ModelStatistic> findModelStatisticAndSort(Sort by);

	default public List<Model> findMaxModelCtiteria(MaxCriteria criteria) {
		Sort sort = null;
		switch (criteria){ 
			case COUNT:  sort = Sort.by("rentCount").descending(); break;
			case SUMM:  sort = Sort.by("sumSale").descending(); break;
			default:  sort = Sort.by("rentCount").descending();
			}
		try (var modelStatisticStream = findModelStatisticAndSort(sort)) {
			var iterator = modelStatisticStream.iterator();
			var list = new LinkedList<Model>();
			var maxCount = 0.;
			while (iterator.hasNext()) {
				ModelStatistic modelStatistic = iterator.next();
				var model = modelStatistic.getModel();
				var value = criteria == MaxCriteria.COUNT? modelStatistic.getRentCount() : modelStatistic.getSumSale();
				if (maxCount == 0) {
					maxCount = value;
					list.add(model);
				} else if (maxCount == value)
					list.add(model);
				else
					break;
			}
			return list;
		}
	}	
	
	List<RentRecord> findAllByCarIdCarAndRentDateBetween(String carId, LocalDate begin, LocalDate end);
	List<RentRecord> findAllByCarIdCar(String carId);
	
	@Query("SELECT COALESCE(SUM(r.total),0) FROM #{#entityName} r WHERE r.recordClosed = true AND  (r.rentDate BETWEEN ?2 AND ?3) AND r.car.idCar = ?1")
	double findTotalByCarInRangeDate(String carId, LocalDate begin, LocalDate end);

//	@Query("SELECT c AS car, COALESCE(SUM(r.rentDurationDays),0) AS countDay, SUM(r.total-r.damageRepairPrice) AS sumSale FROM #{#entityName} r JOIN r.car c GROUP BY r.car ")
	@Query("SELECT c AS car, SUM(r.rentDurationDays) AS countDay, SUM(r.total)-SUM(r.damageRepairPrice) AS sumSale FROM  #{#entityName} r JOIN r.car c GROUP BY r.car ")
	Stream<CarStatistic> findCarStatisticAndSort(Sort order);

	default public List<Car> findMaxCarsCriteria(MaxCriteria criteria) {
		Sort sort = null;
		switch (criteria){ 
			case COUNT_DAY:  sort = Sort.by("countDay").descending(); break;
			case SUMM:  sort = Sort.by("sumSale").descending(); break;
			default:  sort = Sort.by("countDay").descending();
			}
		try (var carStatisticSrtream = findCarStatisticAndSort(sort)) {
			var iterator = carStatisticSrtream.iterator();
			var list = new LinkedList<Car>();
			var maxCountDay = 0.;
			while (iterator.hasNext()) {
				CarStatistic carStatistic = iterator.next();
				var value = criteria == MaxCriteria.COUNT_DAY? carStatistic.getCountDay() : carStatistic.getSumSale();
				if (maxCountDay == 0.) {
					maxCountDay = value;
					list.add(carStatistic.getCar());
				} else if (maxCountDay == value)
					list.add(carStatistic.getCar());
				else
					break;
			}
			return list;
		}
	}

	List<RentRecord> findAllByDamageRepairPriceGreaterThanAndRentDateBetween(double d, LocalDate begin, LocalDate end);

	@Query("SELECT d AS driver, COUNT(r) AS rentCount, SUM(r.total)-SUM(r.damageRepairPrice) AS sumSale, SUM(r.damageRepairPrice) AS sumDamage FROM #{#entityName} r JOIN r.driver d GROUP BY r.driver")
	Stream<DriverStatistic> findDriverStatisticAndSort(Sort oreder);
	
	default public List<Driver> findAllMaxPayDrivers(MaxCriteria criteria){
		Sort sort = null;
		switch (criteria){ 
			case COUNT:  sort = Sort.by("rentCount").descending(); break;
			case SUMM:  sort = Sort.by("sumSale").descending(); break;
			case DAMAGE: sort = Sort.by("sumDamage").descending();break;
			default:  sort = Sort.by("rentCount").descending();
			}
		try (var driverStatisticSrtream = findDriverStatisticAndSort(sort)) {
			var iterator = driverStatisticSrtream.iterator();
			var list = new LinkedList<Driver>();
			var maxSum = 0.;
			while (iterator.hasNext()) {
				var driverStatistic = iterator.next();
				var value = criteria == MaxCriteria.SUMM ? driverStatistic.getSumSale() : 
					criteria == MaxCriteria.DAMAGE ? driverStatistic.getSumDamage() : driverStatistic.getRentCount();
				if (maxSum == 0.) {
					maxSum = value;
					list.add(driverStatistic.getDriver());
				} else if (maxSum == value)
					list.add(driverStatistic.getDriver());
				else
					break;
			}
			return list;
		}		
	}
	
	@Query("SELECT COALESCE(SUM(r.total),0) FROM #{#entityName} r WHERE r.recordClosed = true AND r.rentDate BETWEEN ?1 AND ?2")
	double findTotalInRangeDate(LocalDate begin, LocalDate end);

	@Query("SELECT COALESCE(SUM(r.rentDurationDays*m.dailyRate),0) FROM #{#entityName} r JOIN r.car.model m WHERE r.recordClosed = true AND  r.rentDate BETWEEN ?1 AND ?2")
	double findIncomeInRangeDate(LocalDate begin, LocalDate end);
	
	@Query("SELECT COALESCE(SUM((r.returnDate - (r.rentDate + r.rentDurationDays))*m.dailyRate*?#{config.getDelayPenalty()}),0)  FROM #{#entityName} r JOIN r.car.model m WHERE r.recordClosed = true AND  r.rentDate BETWEEN ?1 AND ?2")
	double findSumDelayInRangeDate(LocalDate begin, LocalDate end);

	@Query("SELECT COALESCE(SUM((1-r.fuelInTank/100)*m.tankVolume*?#{config.getFuelPrice()}),0)  FROM #{#entityName} r JOIN r.car.model m WHERE r.recordClosed = true AND  r.rentDate BETWEEN ?1 AND ?2")
	double findFuelFeesInRangeDate(LocalDate begin, LocalDate end);

	@Query("SELECT COALESCE(SUM(r.damageRepairPrice),0)  FROM #{#entityName} r WHERE r.recordClosed = true AND  r.rentDate BETWEEN ?1 AND ?2")
	double findDamagesInRangeDate(LocalDate begin, LocalDate end);

	@Query("SELECT COALESCE(SUM(r.rentDurationDays*m.dailyRate + (r.returnDate - (r.rentDate + r.rentDurationDays))*m.dailyRate*?#{config.getDelayPenalty()}),0) FROM #{#entityName} r JOIN r.car.model m WHERE r.recordClosed = true AND  r.car.idCar = ?1 AND r.rentDate BETWEEN ?2 AND ?3")
	double findIncomeByCarinDateRange(String carId, LocalDate begin, LocalDate end);

	@Query("SELECT COALESCE(SUM(r.rentDurationDays*m.dailyRate + (r.returnDate - (r.rentDate + r.rentDurationDays))*m.dailyRate*?#{config.getDelayPenalty()}),0) FROM #{#entityName} r JOIN r.car.model m WHERE r.recordClosed = true AND  r.car.model.idModel = ?1 AND r.rentDate BETWEEN ?2 AND ?3")
	double findIncomeByModelInDateRange(String modelId, LocalDate begin, LocalDate end);
	
	@Query("SELECT COUNT(r) FROM  #{#entityName} r WHERE r.car.idCar = ?1 AND ?2 BETWEEN r.rentDate AND COALESCE(r.endRepairDate, r.returnDate, CURRENT_DATE)")
	Integer findByCarUseOnDate(String carId, LocalDate useDate);

	// for Test only

	void deleteByCarIdCar(String carId);
	void deleteByDriverIdDriver(String diverId);
}
