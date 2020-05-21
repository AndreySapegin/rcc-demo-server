package app.business.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data

@Entity
@Table(name = "rccConfig")

public class RccConfig {
	
	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id = 1;
	
	private double delayPenalty;
	private double fuelPrice;


	public RccConfig(double delayPenalty, double fuelPrice) {
		super();
		this.delayPenalty = delayPenalty;
		this.fuelPrice = fuelPrice;
	}

}
