package app.business.dto;

import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import app.business.entities.Car;
import app.business.entities.Driver;
import app.business.entities.Model;
import app.business.entities.RccConfig;
import app.business.entities.RentRecord;
import app.business.repositories.ModelRepository;

@Service
public class DtoService {

	@Autowired ModelRepository modelRepo;
	
	public RccConfig getRccConfig(RccConfigDTO config) {
		return new RccConfig(config.getDelayPenalty(), config.getFuelPrice());
	}
	
	public RccConfigDTO getRccConfigDTO(RccConfig config) {
		return new RccConfigDTO(config.getDelayPenalty(), config.getFuelPrice());
	}
	
	public Car getCar(RccCarDTO car) {
		var carEntity = new Car(car.getIdCar());
		carEntity.setInUse(car.isInUse());
		carEntity.setWritenOff(car.isWritenOff());
		carEntity.setModel(modelRepo.findById(car.getModel().getIdModel())
				.orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("DTO-Entity-Convertion: Model %s not found", car.getModel().getIdModel()))));
		return carEntity;
	}
	
	public RccCarDTO getCarDTO(Car car) {
		return new RccCarDTO(car.getIdCar(), car.isInUse(),car.isWritenOff(), getModelDTO(car.getModel()));
	}
	public Model getModel(RccModelDTO model) {
		var modelEntity = new Model(model.getIdModel());
		modelEntity.setDailyRate(model.getDailyRate());
		modelEntity.setTankVolume(model.getTankVolume());
		if (model.getCars() != null) {
			modelEntity.setCars(new HashSet<Car>(model.getCars().stream().map(c-> new Car(c)).collect(Collectors.toSet())));
		}
		return modelEntity;
	}
	
	public RccModelDTO getModelDTO(Model model) {
		return new RccModelDTO(model.getIdModel(),model.getDailyRate(),model.getTankVolume());
	}

	public Driver getDriver(RccDriverDTO driver) {
		return new Driver(driver.getIdDriver(),driver.getBirthDay(),driver.getPhoneNumber(),driver.getEMail());
	}

	public RccDriverDTO getDriverDTO(Driver driver) {
		return new RccDriverDTO(driver.getIdDriver(),
				driver.getBirthDay(),
				driver.getPhoneNumber(),
				driver.getEMail(),
				driver.getRecords() == null? new HashSet<Integer>() : driver.getRecords().stream().map(RentRecord::getIdRecord).collect(Collectors.toSet()));
	}	
	
	public RccRentRecordDTO getRentRecordDTO(RentRecord record) {
		return new RccRentRecordDTO(record.getIdRecord(),
				getDriverDTO(record.getDriver()),
				getCarDTO(record.getCar()),
				record.getRentDate(),
				record.getRentDurationDays(),
				record.getReturnDate(),
				record.getEndRepairDate(),
				record.getFuelInTank(),
				record.getDamageRepairPrice(),
				record.getTotal(),
				record.isReceived(),
				record.isRecordClosed());
	}


}
