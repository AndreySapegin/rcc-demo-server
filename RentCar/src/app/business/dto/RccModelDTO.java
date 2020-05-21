package app.business.dto;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
@EqualsAndHashCode(of = "idModel")

public class RccModelDTO {

	@NotBlank
	@Size(min=6,max=20)
	private String idModel;
	@Min(1)
	private double dailyRate = -1;
	@Min(10) @Max(200)
	private double tankVolume = -1;
	
	private Set<String> cars = new HashSet<>();
	
	public RccModelDTO(String idModel, double dailyRate, double tankVolume) {
		super();
		this.idModel = idModel;
		this.dailyRate = dailyRate;
		this.tankVolume = tankVolume;
	}
	
	

}
