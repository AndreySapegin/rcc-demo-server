package app.business.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
@EqualsAndHashCode(of = "idCar")

public class RccCarDTO {

	@NotBlank
	@Size(max=20)
	private String idCar;
	private boolean inUse = false;
	private boolean isWritenOff;
	@NotNull
	private RccModelDTO model;
	
}
