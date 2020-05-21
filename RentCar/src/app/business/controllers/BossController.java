package app.business.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import app.business.dto.DtoService;
import app.business.dto.RccCarDTO;
import app.business.dto.RccConfigDTO;
import app.business.dto.RccModelDTO;
import app.business.services.BossService;

@RestController
@CrossOrigin
@RequestMapping("/boss")
public class BossController {

	
		@Autowired BossService service;
		@Autowired DtoService convert;
		
		@PostMapping("st_cfg")
		public RccConfigDTO setConfig(@RequestBody RccConfigDTO config) {
			return convert.getRccConfigDTO(service.setConfig(config));
		}
		
		@PostMapping("st_dly_price")
		public RccModelDTO setModelDayRentPrice(@RequestBody @Valid RccModelDTO model, @RequestParam double dailyPrice) {
			return convert.getModelDTO(service.setModelDayRentPrice(model, dailyPrice));
		}
		
		@PostMapping("woff_cr")
		public RccCarDTO writeOffCar(@RequestBody @Valid RccCarDTO car) {
			return convert.getCarDTO(service.writeOffCar(car));
		}
		
		@GetMapping("gt_cfg")
		public RccConfigDTO getConfig() {
			return convert.getRccConfigDTO(service.getConfigProperty());
		}
}
