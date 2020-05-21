package app.business.controllers;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import app.business.dto.DtoService;
import app.business.dto.RccCarDTO;
import app.business.dto.RccModelDTO;
import app.business.dto.RccRentRecordDTO;
import app.business.services.ManagerService;

@RestController
@RequestMapping("mng")
@Validated
public class ManagerController {

	@Autowired ManagerService service;
	@Autowired DtoService convert;
	
	@GetMapping("gt_rcd_unrec")
	public List<RccRentRecordDTO> getRecordsUnreceived(){
		return service.getRecordsUnreceived().stream().map(convert::getRentRecordDTO).collect(Collectors.toList());
	}
	@GetMapping("gt_rcd_uncls")
	public List<RccRentRecordDTO> getRecordsUnclosed(){
		return service.getRecordsUnclosed().stream().map(convert::getRentRecordDTO).collect(Collectors.toList());
	}
	@GetMapping("gt_all_cr")
	public List<RccCarDTO> getAllCars(){
		return service.getAllCars();
	}
	
	@GetMapping("gt_crs_inuse")
	public List<RccCarDTO> getCarsInUse(){
		return service.getCarsInUse().stream().map(convert::getCarDTO).collect(Collectors.toList());
	}
	@GetMapping("gt_crs_woff")
	public List<RccCarDTO> getCarsWrittenOff(){
		return service.getCarsWrittenOff().stream().map(convert::getCarDTO).collect(Collectors.toList());
	}
	
	@PostMapping("add_mdl")
	public RccModelDTO addModel(@RequestBody @Valid RccModelDTO model) {
		return convert.getModelDTO(service.addModel(model));
	}
	@PostMapping("add_cr")
	public RccCarDTO addCar(@RequestBody @Valid RccCarDTO car) {
		return convert.getCarDTO(service.addCar(car));
	}
	@PostMapping("st_dmg_cost")
	public RccRentRecordDTO setDamagesRepairPrice(@RequestBody @Valid RccRentRecordDTO record, @RequestParam @Positive double cost) {
		return convert.getRentRecordDTO(service.setDamagesRepairPrice(record.getIdRecord(), cost, record.getEndRepairDate()));
	}
	@GetMapping("cls_rcd") 
	public RccRentRecordDTO closeRecord(@RequestParam @Positive int recordId, @RequestParam @Positive double total) {
		return convert.getRentRecordDTO(service.closeRecord(recordId, total));
	}
}
