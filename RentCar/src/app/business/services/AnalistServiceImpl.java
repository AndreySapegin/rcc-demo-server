package app.business.services;


import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.business.entities.Car;
import app.business.entities.Driver;
import app.business.entities.Model;
import app.business.entities.RentRecord;
import app.business.repositories.CarRepository;
import app.business.repositories.DriverRepository;
import app.business.repositories.MaxCriteria;
import app.business.repositories.ModelRepository;
import app.business.repositories.RentRecordRepository;
@Service
public class AnalistServiceImpl implements AnalistService {

	@Autowired CarRepository carRepo;
	@Autowired DriverRepository driverRepo;
	@Autowired ModelRepository modelRepo;
	@Autowired RentRecordRepository recordRepo;
	
	@Override
	public List<RentRecord> getRecordsInRentDateRange(LocalDate begin, LocalDate end) {
		return recordRepo.findAllByRentDateBetween(begin,  end);
	}

	@Override
	public List<RentRecord> getRecordsDelayedInRentDateRange(LocalDate begin, LocalDate end) {
		return recordRepo.findAllDelayedAndRentedInDateRange(begin, end);
	}

	@Override
	public List<RentRecord> getRecordsUnreceived() {
		return recordRepo.findAllByReceived(false);
	}

	@Override
	public List<RentRecord> getRecordsUnclosed() {
		return recordRepo.findAllByRecordClosed(false);
	}

	@Override
	public List<Model> getAllModels() {
		return modelRepo.findAll();
	}

	@Override
	public List<Model> getModelsAvailable() {
		return carRepo.findAllAvilableByModel();
	}

	@Override
	public List<Model> getModelsEmpty() {
		return modelRepo.findAllModelsWithoutCars();
	}

	@Override
	public List<RentRecord> getRecordsByModelInDateRange(String modelId, LocalDate begin, LocalDate end) {
		return recordRepo.findAllByCarModelIdModelAndRentDateBetween(modelId, begin, end);
	}

	@Override
	public double getTotalByModelInDateRange(String modelId, LocalDate begin, LocalDate end) {
		return recordRepo.findTotalByModelInRange(modelId, begin, end);
	}

	@Override
	public double getIncomeByModelInDateRange(String modelId, LocalDate begin, LocalDate end) {
		return recordRepo.findIncomeByModelInDateRange(modelId, begin, end);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Model> getMostUsedModels() {
		return recordRepo.findMaxModelCtiteria(MaxCriteria.COUNT);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Model> getMostSaledModels() {
		return recordRepo.findMaxModelCtiteria(MaxCriteria.SUMM);
	}

	@Override
	public List<Car> getAllCars() {
		return carRepo.findAll();
	}

	@Override
	public List<Car> getCarsByModel(String modelId) {
		return carRepo.findAllByModel(modelId);
	}

	@Override
	public List<RentRecord> getRecordsByCarInDateRange(String carId, LocalDate begin, LocalDate end) {
		return recordRepo.findAllByCarIdCarAndRentDateBetween(carId, begin, end);
	}

	@Override
	public double getTotalByCarInDateRange(String carId, LocalDate begin, LocalDate end) {
		return recordRepo.findTotalByCarInRangeDate(carId, begin, end);
	}

	@Override
	public double getIncomeByCarInDateRange(String carId, LocalDate begin, LocalDate end) {
		return recordRepo.findIncomeByCarinDateRange(carId, begin, end);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Car> getMostUsedCars() {
		return recordRepo.findMaxCarsCriteria(MaxCriteria.COUNT_DAY);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Car> getMostSaledCars() {
		return recordRepo.findMaxCarsCriteria(MaxCriteria.SUMM);
	}

	@Override
	public List<RentRecord> getRecordsDelayedInDateRange(LocalDate begin, LocalDate end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RentRecord> getRecordsDamagedInDateRange(LocalDate begin, LocalDate end) {
		return recordRepo.findAllByDamageRepairPriceGreaterThanAndRentDateBetween(0.,begin,end);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Driver> getMostPayDrivers() {
		return recordRepo.findAllMaxPayDrivers(MaxCriteria.SUMM);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Driver> getMostSloppyDrivers() {
		return recordRepo.findAllMaxPayDrivers(MaxCriteria.DAMAGE);
	}

	@Override
	public double getTotalInDateRange(LocalDate begin, LocalDate end) {
		return recordRepo.findTotalInRangeDate(begin,end);
	}

	@Override
	public double getIncomeInDateRange(LocalDate begin, LocalDate end) {
		return recordRepo.findIncomeInRangeDate(begin,end);
	}

	@Override
	public double getTotalDelayInDateRange(LocalDate begin, LocalDate end) {
		return recordRepo.findSumDelayInRangeDate(begin,end);
	}

	@Override
	public double getTotalFuelFeesInDateRange(LocalDate begin, LocalDate end) {
		return recordRepo.findFuelFeesInRangeDate(begin,end);
	}

	@Override
	public double getTotalDamagesRepairPriceInDateRange(LocalDate begin, LocalDate end) {
		return recordRepo.findDamagesInRangeDate(begin,end);
	}


}
