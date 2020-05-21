package app.business.services;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import app.business.dto.DtoService;
import app.business.dto.RccCarDTO;
import app.business.dto.RccModelDTO;
import app.business.dto.RccRentRecordDTO;
import app.business.entities.Car;
import app.business.entities.Model;
import app.business.entities.RentRecord;
import app.business.repositories.CarRepository;
import app.business.repositories.ModelRepository;
import app.business.repositories.RentRecordRepository;

@Service
public class ManagerServiceImpl implements ManagerService {

	@Autowired RentRecordRepository recRepo;
	@Autowired CarRepository carRepo;
	@Autowired ModelRepository modelRepo;
	@Autowired DtoService convert;
	
	@Override
	public List<RentRecord> getRecordsUnreceived(){
		return recRepo.findAllByReceived(false);
	}
	
	@Override
	public List<RentRecord> getRecordsUnclosed(){
		return recRepo.findAllByRecordClosed(false);
	}
	@Override
	@Transactional(readOnly = true)
	public List<RccCarDTO> getAllCars() {
		return carRepo.findAllStream().map(convert::getCarDTO).collect(Collectors.toList());
	}
	@Override
	public List<Car> getCarsInUse(){
		return carRepo.findAllByInUse(true);
	}
	
	@Override
	public List<Car> getCarsWrittenOff(){
		return carRepo.findAllByIsWritenOff(true);
	}
	
	@Override
	public Model addModel(RccModelDTO model) {
		var modelEntity = modelRepo.findById(model.getIdModel()).orElseGet(() -> new Model(model.getIdModel()));
		modelEntity.setDailyRate(model.getDailyRate());
		modelEntity.setTankVolume(model.getTankVolume());
		return modelRepo.save(modelEntity);
	}
	
	@Override
	public Car addCar(RccCarDTO car) {
		var carEntity = carRepo.findById(car.getIdCar()).orElseGet(() -> new Car(car.getIdCar()));
		carEntity.setInUse(car.isInUse());
		carEntity.setWritenOff(car.isWritenOff());
		if (car.getModel() != null) {carEntity.setModel(modelRepo.findById(car.getModel().getIdModel())
			.orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("add-car: Model %s not found",car.getModel().getIdModel()))));}
		else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("add-car: Model not selected for car %s",car.getIdCar()));
		return carRepo.save(carEntity);
	}
	
	@Override
	@Transactional
	public RentRecord setDamagesRepairPrice(int recordId, double cost, LocalDate repairDate) {
		var recEntity = recRepo.findById(recordId)
				.orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST,String.format("set-damage: Record %d not found",recordId)));
		recEntity.setDamageRepairPrice(cost);
		recEntity.setEndRepairDate(repairDate);
		recEntity.setTotal(recEntity.getTotal() + cost);
		recEntity.getCar().setInUse(false);
		return recRepo.save(recEntity);
	}
	
	@Override
	public RentRecord closeRecord(int recordId, double total) {
		var recEntity = recRepo.findById(recordId)
				.orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST,String.format("set-close: Record %d not found",recordId)));
		if (recEntity.getTotal() != total)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("set-close: Total cost is not equals saved in record %d",recordId));
		recEntity.setRecordClosed(true);
		return recRepo.save(recEntity);
		
	}	
}
