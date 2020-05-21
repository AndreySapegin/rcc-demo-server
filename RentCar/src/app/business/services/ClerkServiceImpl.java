package app.business.services;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import app.business.dto.DtoService;
import app.business.dto.RccCarDTO;
import app.business.dto.RccDriverDTO;
import app.business.dto.RccModelDTO;
import app.business.dto.RccRentRecordDTO;
import app.business.entities.Car;
import app.business.entities.Driver;
import app.business.entities.Model;
import app.business.entities.RentRecord;
import app.business.repositories.CarRepository;
import app.business.repositories.DriverRepository;
import app.business.repositories.RentRecordRepository;
import app.config.DatabaseProperty;

@Service
public class ClerkServiceImpl implements ClerkService {

	@Autowired RentRecordRepository recordRepo;
	@Autowired CarRepository carRepo;
	@Autowired DriverRepository driverRepo;
	@Autowired DtoService convert;
	@Autowired DatabaseProperty config;
	
	@Override
	@Transactional
	public RentRecord rentCar(RccRentRecordDTO record) {
		if (record.getIdRecord() > 0) 
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("add-record: Wrong id record in new record, Must be null", ""));
		var recordEntity = new RentRecord();
		recordEntity.setCar(carRepo.findById(record.getCar().getIdCar())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("add-record: Wrong %s car id in new record", record.getCar().getIdCar()))));
		if (recordEntity.getCar().isWritenOff())
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("add-record: Car %s has write off status",recordEntity.getCar().getIdCar()));
		if (recordRepo.findByCarUseOnDate(recordEntity.getCar().getIdCar(),record.getRentDate()) != 0)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("add-record: Car %s has in use status",recordEntity.getCar().getIdCar()));
		recordEntity.setDriver(driverRepo.findById(record.getDriver().getIdDriver())
				.orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("add-record: Wrong %s driver id in new record", record.getDriver().getIdDriver()))));
		recordEntity.setRentDate(record.getRentDate());
		recordEntity.setRentDurationDays(record.getRentDurationDays());
		recordEntity.setFuelInTank(record.getFuelInTank());
		recordEntity.getCar().setInUse(true);
		return recordRepo.save(recordEntity);
	}

	@Override
	public List<Model> getModelsAvailable(){
		return carRepo.findAllAvilableByModel();
	}	
	
	@Override
	public List<Car> getModelsAvailableByModel(List<RccModelDTO> list){
		var listId = list.stream().map(RccModelDTO::getIdModel).collect(Collectors.toList());
		return carRepo.findAllByInUseAndModelIn(false, listId);
	}
	
	@Override
	public List<Car> getCarsAvailable(){
		return carRepo.findAllByInUse(false);
	}
	@Override
	public List<Car> getCarsAvailableByModel(String modelId){
		return carRepo.findAllByInUseAndModelIdModel(false, modelId);
	}
	
	@Override
	public Car getCar(String carId) {
		return carRepo.findById(carId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("get-car: Wrong %s car id", carId)));
	}
	
	@Override
	public Driver addDriver(RccDriverDTO driver) {
		driverRepo.findById(driver.getIdDriver()).ifPresent( d -> 
			{throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("add-driver: Driver with id %s already exist", d.getIdDriver()));});
		return driverRepo.save(convert.getDriver(driver));
	}
	
	@Override
	public Driver getDriver(String driverId) {
		return driverRepo.findById(driverId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("get-driver: Wrong %s driver id", driverId)));
	}
	
	@Override
	public Driver correctDriver(RccDriverDTO driver) {
		var driverEntity = driverRepo.findById(driver.getIdDriver())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("correct-driver: Wrong %s driver id", driver.getIdDriver())));
		driverEntity.setPhoneNumber(driver.getPhoneNumber());
		driverEntity.setBirthDay(driver.getBirthDay());
		driverEntity.setEMail(driver.getEMail());
		return driverRepo.save(driverEntity);
	}
	
	@Override
	public List<RentRecord> getRecordsDelayedByDriver(String driverId, LocalDate onDate){
		return recordRepo.findAllDelayedByDriverOnDate(driverId, onDate);
	}
	
	@Override
	public List<RentRecord> getRecordsByDriverInRentDateRange(String driverId, LocalDate beginRange, LocalDate endRange){
		return recordRepo.findAllByDriverIdDriverAndRentDateBetween(driverId, beginRange, endRange);
	}
	
	@Override
	@Transactional
	public RentRecord receiveCar(RccRentRecordDTO record){
		var recordEntity = recordRepo.findById(record.getIdRecord()).orElseThrow(() ->
			 new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("edit-record: Record with id %s not exist", record.getIdRecord())));
		var total = recordEntity.getRentDurationDays() * recordEntity.getCar().getModel().getDailyRate();
		if (recordEntity.getFuelInTank() != 100.) total += (1 - recordEntity.getFuelInTank()/100.) * recordEntity.getCar().getModel().getTankVolume() * config.getFuelPrice();
		var planedDate= recordEntity.getRentDate().plusDays(recordEntity.getRentDurationDays());
		if (planedDate.isBefore(record.getReturnDate()))
				total+= ChronoUnit.DAYS.between(planedDate, record.getReturnDate())*recordEntity.getCar().getModel().getDailyRate()*config.getDelayPenalty();
		recordEntity.setReturnDate(record.getReturnDate());
		recordEntity.setTotal(total);
		recordEntity.setReceived(true);
		var carEntity = recordEntity.getCar();
		if (recordEntity.getDamageRepairPrice() == 0. && recordRepo.findByCarUseOnDate(carEntity.getIdCar(), LocalDate.now()) == 0) {
			carEntity.setInUse(false);
//			carRepo.save(carEntity);
			}
		return recordEntity;
		
	}
	
	@Override
	public RentRecord setTankFull(RccRentRecordDTO record) {
		var recordEntity = recordRepo.findById(record.getIdRecord()).orElseThrow(() ->
		 new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("edit-record: Record with id %s not exist", record.getIdRecord())));
		recordEntity.setFuelInTank(100.);
		return recordRepo.save(recordEntity);		
	}
	
	@Override
	public RentRecord setFuelInTank(RccRentRecordDTO record, double volume) {
		var recordEntity = recordRepo.findById(record.getIdRecord()).orElseThrow(() ->
		 new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("edit-record: Record with id %s not exist", record.getIdRecord())));
		recordEntity.setFuelInTank(volume);
		return recordRepo.save(recordEntity);		
		
	}
	@Override
	public RentRecord setNoDamages(RccRentRecordDTO record) {
		var recordEntity = recordRepo.findById(record.getIdRecord()).orElseThrow(() ->
		 new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("edit-record: Record with id %s not exist", record.getIdRecord())));
		recordEntity.setDamageRepairPrice(0.);;
		return recordRepo.save(recordEntity);		
	}

	@Override
	public double getRecordTotal(RccRentRecordDTO record) {
		var recordEntity = recordRepo.findById(record.getIdRecord()).orElseThrow(() ->
		 new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("edit-record: Record with id %s not exist", record.getIdRecord())));
		return recordEntity.getTotal();
	}
}
