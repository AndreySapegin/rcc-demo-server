package app.business.services;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import app.business.dto.RccCarDTO;
import app.business.dto.RccModelDTO;
import app.business.dto.RccRentRecordDTO;
import app.business.entities.Car;
import app.business.entities.Model;
import app.business.entities.RentRecord;

public interface ManagerService {

	List<RentRecord> getRecordsUnreceived();

	List<RentRecord> getRecordsUnclosed();

	List<RccCarDTO> getAllCars();

	List<Car> getCarsInUse();

	List<Car> getCarsWrittenOff();

	Model addModel(RccModelDTO model);

	Car addCar(RccCarDTO car);

	RentRecord setDamagesRepairPrice(int record, double cost, LocalDate repairdate);

	RentRecord closeRecord(int recordId, double total);

}