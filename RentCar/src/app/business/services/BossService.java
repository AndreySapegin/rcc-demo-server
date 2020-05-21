package app.business.services;

import app.business.dto.RccCarDTO;
import app.business.dto.RccConfigDTO;
import app.business.dto.RccModelDTO;
import app.business.entities.Car;
import app.business.entities.Model;
import app.business.entities.RccConfig;

public interface BossService {

	RccConfig setConfig(RccConfigDTO config);

	Model setModelDayRentPrice(RccModelDTO model, double dailyPrice);

	Car writeOffCar(RccCarDTO car);

	Model addModel(RccModelDTO model);

	RccConfig getConfigProperty();

}