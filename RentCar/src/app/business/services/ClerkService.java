package app.business.services;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import app.business.dto.RccCarDTO;
import app.business.dto.RccDriverDTO;
import app.business.dto.RccModelDTO;
import app.business.dto.RccRentRecordDTO;
import app.business.entities.Car;
import app.business.entities.Driver;
import app.business.entities.Model;
import app.business.entities.RentRecord;

public interface ClerkService {

	RentRecord rentCar(RccRentRecordDTO record);

	List<Model> getModelsAvailable();

	List<Car> getCarsAvailable();

	List<Car> getCarsAvailableByModel(String modelId);

	Car getCar(String carId);

	Driver addDriver(RccDriverDTO driver);

	Driver getDriver(String driverId);

	Driver correctDriver(RccDriverDTO driver);

	List<RentRecord> getRecordsDelayedByDriver(String driverId, LocalDate onDate);

	List<RentRecord> getRecordsByDriverInRentDateRange(String driverId, LocalDate beginRange, LocalDate endRange);

	RentRecord receiveCar(RccRentRecordDTO record);

	RentRecord setTankFull(RccRentRecordDTO record);

	RentRecord setFuelInTank(RccRentRecordDTO record, double volume);

	RentRecord setNoDamages(RccRentRecordDTO record);

	double getRecordTotal(RccRentRecordDTO record);

	List<Car> getModelsAvailableByModel(List<RccModelDTO> list);

}