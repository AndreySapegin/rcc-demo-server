package app.business.controllers;

import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

import static java.util.stream.Collectors.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import app.business.dto.DtoService;
import app.business.dto.RccCarDTO;
import app.business.dto.RccDriverDTO;
import app.business.dto.RccModelDTO;
import app.business.dto.RccRentRecordDTO;
import app.business.services.ClerkService;

@RestController
@RequestMapping("stf")
@Validated
public class ClerkController {

	@Autowired ClerkService service;
	@Autowired DtoService convert;
	
	@PostMapping("new_rnt")
	public RccRentRecordDTO rentCar(@RequestBody @Valid RccRentRecordDTO record) {
		return convert.getRentRecordDTO(service.rentCar(record));
	}
	
	@PostMapping("gt_free_by_mdl")
	public List<RccCarDTO> getModelsAvailableByModel(@RequestBody List<RccModelDTO> list){
		return service.getModelsAvailableByModel(list).stream().map(convert::getCarDTO).collect(toList());
	}
	@GetMapping("gt_free_mdl")
	public List<RccModelDTO> getModelsAvailable(){
		return service.getModelsAvailable().stream().map(convert::getModelDTO).collect(toList());
	}	
	
	@GetMapping("gt_crs_avlb")
	public List<RccCarDTO> getCarsAvailable(){
		return service.getCarsAvailable().stream().map(convert::getCarDTO).collect(toList());
	}
	@GetMapping("gt_crs_avlb_by_mdlid")
	public List<RccCarDTO> getCarsAvailableByModel(@NotBlank String modelId){
		return service.getCarsAvailableByModel(modelId).stream().map(convert::getCarDTO).collect(toList());
	}
	
	@GetMapping("gt_cr_by_id")
	public RccCarDTO getCar(@NotBlank String carId) {
		return convert.getCarDTO(service.getCar(carId));
	}
	
	@PostMapping("new_drv")
	public RccDriverDTO addDriver(@RequestBody @Valid RccDriverDTO driver) {
		return convert.getDriverDTO(service.addDriver(driver));
	}
	
	@GetMapping("gt_drv_by_id")
	public RccDriverDTO getDriver(@NotBlank String driverId) {
		return convert.getDriverDTO(service.getDriver(driverId));
	}
	
	@PostMapping("upd_drv")
	public RccDriverDTO correctDriver(@RequestBody @Valid RccDriverDTO driver) {
		return convert.getDriverDTO(service.correctDriver(driver));
	}

	@GetMapping("gt_rcd_drvid")
	public List<RccRentRecordDTO> getRecordsDelayedByDriver( @NotBlank String driverId,@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate onDate){
		return service.getRecordsDelayedByDriver(driverId, onDate).stream().map(convert::getRentRecordDTO).collect(toList());
	}
	
	@GetMapping("gt_rds_drvid_inrnge")
	public List<RccRentRecordDTO> getRecordsByDriverInRentDateRange( @NotBlank String driverId, 
			@RequestParam(name = "begin") @DateTimeFormat(iso = ISO.DATE) LocalDate beginRange, 
			@RequestParam(name="end") @DateTimeFormat(iso = ISO.DATE) LocalDate endRange){
		return service.getRecordsByDriverInRentDateRange(driverId, beginRange, endRange).stream().map(convert::getRentRecordDTO).collect(toList());
	}
	@PostMapping("rcv_car")
	public RccRentRecordDTO receiveCar(@RequestBody @Valid RccRentRecordDTO record) {
		return convert.getRentRecordDTO(service.receiveCar(record));
	}
	@PostMapping("st_fulltank")
	public RccRentRecordDTO setTankFull(@RequestBody @Valid RccRentRecordDTO record) {
		return convert.getRentRecordDTO(service.setTankFull(record));
	}
	@PostMapping("st_fuel")
	public RccRentRecordDTO setFuelInTank(@RequestBody @Valid RccRentRecordDTO record, @RequestParam @Positive double volume) {
		return convert.getRentRecordDTO(service.setFuelInTank(record, volume));
	}
	@PostMapping("st_no_damage")
	public RccRentRecordDTO setNoDamages(@RequestBody @Valid RccRentRecordDTO record) {
		return convert.getRentRecordDTO(service.setNoDamages(record));
	}
	
	@PostMapping("gt_total")
	public double getRecordTotal(@RequestBody @Valid RccRentRecordDTO record) {
		return service.getRecordTotal(record);
	}
	
}
