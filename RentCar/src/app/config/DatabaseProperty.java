package app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import app.business.entities.RccConfig;
import app.business.repositories.RccConfigRepository;

@Component("databaseProperty")
public class DatabaseProperty {

	@Autowired RccConfigRepository configRepo;
	

	public double getFuelPrice() {
		RccConfig currentConfig = configRepo.findById(1).orElseGet(RccConfig::new);
		return currentConfig.getFuelPrice();
	}

	public double getDelayPenalty() {
		RccConfig currentConfig = configRepo.findById(1).orElseGet(RccConfig::new);
		return currentConfig.getDelayPenalty();
	}
	
}
