package app.business.controllers;

import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.NotBlank;

import static java.util.stream.Collectors.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import app.business.dto.DtoService;
import app.business.dto.RccCarDTO;
import app.business.dto.RccDriverDTO;
import app.business.dto.RccModelDTO;
import app.business.dto.RccRentRecordDTO;
import app.business.services.AnalistService;

@RestController
@RequestMapping("/atic")
@Validated
public class AnalistController {
	
	@Autowired AnalistService service;
	@Autowired DtoService convert;

	@GetMapping("rds_range")
	public List<RccRentRecordDTO> getRecordsInRentDateRange(@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate begin,
			@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate end){
		return service.getRecordsInRentDateRange(begin, end).stream().map(convert::getRentRecordDTO).collect(toList());
	}
	@GetMapping("rds_dly_range")	
	public List<RccRentRecordDTO> getRecordsDelayedInRentDateRange(@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate begin, 
			@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate end){
		return service.getRecordsDelayedInRentDateRange(begin, end).stream().map(convert::getRentRecordDTO).collect(toList());
	}
	@GetMapping("rds_unrcd")
	public List<RccRentRecordDTO> getRecordsUnreceived(){
		return service.getRecordsUnreceived().stream().map(convert::getRentRecordDTO).collect(toList());
	}
	@GetMapping("rds_uncls")
	public List<RccRentRecordDTO> getRecordsUnclosed(){
		return service.getRecordsUnclosed().stream().map(convert::getRentRecordDTO).collect(toList());
	}
	@GetMapping("mds_all")
	public List<RccModelDTO> getAllModels(){
		return service.getAllModels().stream().map(convert::getModelDTO).collect(toList());
	}
	@GetMapping("mds_free")
	public List<RccModelDTO> getModelsAvailable(){
		return service.getModelsAvailable().stream().map(convert::getModelDTO).collect(toList());
	}
	@GetMapping("mds_w_crs")
	public List<RccModelDTO> getModelsEmpty(){
		return service.getModelsEmpty().stream().map(convert::getModelDTO).collect(toList());
	}
	@GetMapping("rds_md_range")
	public List<RccRentRecordDTO> getRecordsByModelInDateRange(@NotBlank String modelId, 
			@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate begin,
			@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate end){
		return service.getRecordsByModelInDateRange(modelId,begin, end).stream().map(convert::getRentRecordDTO).collect(toList());
	}
	@GetMapping("ttl_md_range")
	public double getTotalByModelInDateRange(@NotBlank String modelId,
			@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate begin,
			@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate end) {
		return service.getTotalByModelInDateRange(modelId, begin, end);
	}
	@GetMapping("inc_md_range")
	public double getIncomeByModelInDateRange(@NotBlank String modelId,
			@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate begin,
			@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate end) {
		return service.getIncomeByModelInDateRange(modelId, begin, end);
	}
	@GetMapping("most_usd_mds")
	public List<RccModelDTO> getMostUsedModels(){
		return service.getMostUsedModels().stream().map(convert::getModelDTO).collect(toList());
	}
	@GetMapping("most_sal_mds")
	public List<RccModelDTO> getMostSaledModels(){
		return service.getMostSaledModels().stream().map(convert::getModelDTO).collect(toList());
	}
	@GetMapping("crs_all")
	public List<RccCarDTO> getAllCars(){
		return service.getAllCars().stream().map(convert::getCarDTO).collect(toList());
	} 
	@GetMapping("crs_md")
	public List<RccCarDTO> getCarsByModel(@NotBlank String modelId){
		return service.getCarsByModel(modelId).stream().map(convert::getCarDTO).collect(toList());
	}
	@GetMapping("rds_cr_range")
	public List<RccRentRecordDTO> getRecordsByCarInDateRange(@NotBlank String carId,
			@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate begin,
			@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate end){
		return service.getRecordsByCarInDateRange( carId,  begin,  end).stream().map(convert::getRentRecordDTO).collect(toList());
	}
	@GetMapping("ttl_cr_range")
	public double getTotalByCarInDateRange(@NotBlank String carId,
			@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate begin,
			@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate end) {
		return service.getTotalByCarInDateRange(carId, begin, end);
	}
	
	@GetMapping("inc_cr_range")
	public double getIncomeByCarInDateRange(@NotBlank String carId,
			@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate begin,
			@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate end) {
		return service.getIncomeByCarInDateRange(carId, begin, end);
	}
	@GetMapping("most_usd_cr")
	public List<RccCarDTO> getMostUsedCars(){
		return service.getMostUsedCars().stream().map(convert::getCarDTO).collect(toList());
	}
	@GetMapping("most_sal_cr")
	public List<RccCarDTO> getMostSaledCars(){
		return service.getMostSaledCars().stream().map(convert::getCarDTO).collect(toList());
	}
	@GetMapping("rds_dly_range_miss")
	public List<RccRentRecordDTO> getRecordsDelayedInDateRange(
			@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate begin,
			@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate end){
		return service.getRecordsDelayedInDateRange(begin,  end).stream().map(convert::getRentRecordDTO).collect(toList());
	}
	@GetMapping("rds_dmg_range")
	public List<RccRentRecordDTO> getRecordsDamagedInDateRange(
			@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate begin,
			@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate end){
		return service.getRecordsDamagedInDateRange(begin,  end).stream().map(convert::getRentRecordDTO).collect(toList());
	}
	@GetMapping("most_pay_drv")
	public List<RccDriverDTO> getMostPayDrivers(){
		return service.getMostPayDrivers().stream().map(convert::getDriverDTO).collect(toList());
	}
	@GetMapping("most_bad_drv")
	public List<RccDriverDTO> getMostSloppyDrivers(){
		return service.getMostSloppyDrivers().stream().map(convert::getDriverDTO).collect(toList());
	}
	@GetMapping("ttl_range")
	public double getTotalInDateRange(
			@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate begin,
			@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate end) {
		return service.getTotalInDateRange(begin, end);
	}
	@GetMapping("inc_range")
	public double getIncomeInDateRange(
			@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate begin,
			@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate end) {
		return service.getIncomeInDateRange(begin, end);
	}
	@GetMapping("dly_range")
	public double getTotalDelayInDateRange(
			@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate begin,
			@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate end) {
		return service.getTotalDelayInDateRange(begin, end);
	}
	@GetMapping("ff_range")
	public double getTotalFuelFeesInDateRange(
			@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate begin,
			@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate end) {
		return service.getTotalFuelFeesInDateRange(begin, end);
	}
	@GetMapping("dmg_range")
	public double getTotalDamagesRepairPriceInDateRange(
			@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate begin,
			@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate end) {
		return service.getTotalDamagesRepairPriceInDateRange(begin, end);
	}
}
