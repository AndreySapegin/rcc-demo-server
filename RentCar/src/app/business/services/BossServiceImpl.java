package app.business.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import app.business.dto.RccCarDTO;
import app.business.dto.RccConfigDTO;
import app.business.dto.RccModelDTO;
import app.business.entities.Car;
import app.business.entities.Model;
import app.business.entities.RccConfig;
import app.business.repositories.CarRepository;
import app.business.repositories.ModelRepository;
import app.business.repositories.RccConfigRepository;

@Service("bossService")
public class BossServiceImpl implements BossService {

		@Autowired ModelRepository modelRepo;
		@Autowired CarRepository carRepo;
		@Autowired RccConfigRepository confRepo;
		
		@Override
		public RccConfig setConfig(RccConfigDTO config) {
			RccConfig currentConfig = confRepo.findById(1).orElseGet(RccConfig::new);
			currentConfig.setDelayPenalty(config.getDelayPenalty());
			currentConfig.setFuelPrice(config.getFuelPrice());
			currentConfig.setId(1);
			return confRepo.save(currentConfig);
		} 
		
		@Override
		public Model setModelDayRentPrice(RccModelDTO model, double dailyPrice) {
			var modelEntity =  modelRepo.findById(model.getIdModel())
					.orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Model %s not found",model.getIdModel())));
			modelEntity.setDailyRate(dailyPrice);
			return modelRepo.save(modelEntity);
		}
		
		@Override
		public Model addModel(RccModelDTO model) {
			var modelEntity = modelRepo.findById(model.getIdModel()).orElseGet(() -> new Model(model.getIdModel()));
			modelEntity.setDailyRate(model.getDailyRate());
			modelEntity.setTankVolume(model.getTankVolume());
			return modelRepo.save(modelEntity);
		}
		
		@Override
		public Car writeOffCar(RccCarDTO car) {
			var carEntity = carRepo.findById(car.getIdCar())
					.orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("Car %s not found",car.getIdCar())));
			carEntity.setWritenOff(true);
			return carRepo.save(carEntity);

		}
		
		@Override
		public RccConfig getConfigProperty() {
			return confRepo.findById(1).orElseGet(RccConfig::new);
		}

}
