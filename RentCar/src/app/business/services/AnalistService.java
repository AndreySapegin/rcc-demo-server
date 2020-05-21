package app.business.services;

import java.time.LocalDate;
import java.util.List;

import app.business.entities.Car;
import app.business.entities.Driver;
import app.business.entities.Model;
import app.business.entities.RentRecord;

public interface AnalistService {
		
	public List<RentRecord> getRecordsInRentDateRange(LocalDate begin, LocalDate end);
	public List<RentRecord> getRecordsDelayedInRentDateRange(LocalDate begin, LocalDate end);
	public List<RentRecord> getRecordsUnreceived();
	public List<RentRecord> getRecordsUnclosed();
	
	public List<Model> getAllModels();
	public List<Model> getModelsAvailable();
	public List<Model> getModelsEmpty();
	public List<RentRecord> getRecordsByModelInDateRange(String modelId, LocalDate begin, LocalDate end);
	public double getTotalByModelInDateRange(String modelId, LocalDate begin, LocalDate end);
	public double getIncomeByModelInDateRange(String modelId, LocalDate begin, LocalDate end);
	public List<Model> getMostUsedModels();
	public List<Model >getMostSaledModels();
	
	public List<Car> getAllCars(); 
	public List<Car> getCarsByModel(String modelId);
	public List<RentRecord> getRecordsByCarInDateRange(String carId, LocalDate begin, LocalDate end);
	public double getTotalByCarInDateRange(String carId, LocalDate begin, LocalDate end);
	public double getIncomeByCarInDateRange(String carId, LocalDate begin, LocalDate end);
	public List<Car> getMostUsedCars();
	public List<Car> getMostSaledCars();
	
	public List<RentRecord> getRecordsDelayedInDateRange(LocalDate begin, LocalDate end);
	public List<RentRecord> getRecordsDamagedInDateRange(LocalDate begin, LocalDate end);
	public List<Driver> getMostPayDrivers();
	public List<Driver> getMostSloppyDrivers();
	
	public double getTotalInDateRange(LocalDate begin, LocalDate end);
	public double getIncomeInDateRange(LocalDate begin, LocalDate end);
	public double getTotalDelayInDateRange(LocalDate begin, LocalDate end);
	public double getTotalFuelFeesInDateRange(LocalDate begin, LocalDate end);
	public double getTotalDamagesRepairPriceInDateRange(LocalDate begin, LocalDate end);
}
