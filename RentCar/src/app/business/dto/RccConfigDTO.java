package app.business.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor

public class RccConfigDTO {
	
	private double delayPenalty;
	private double fuelPrice;

}
