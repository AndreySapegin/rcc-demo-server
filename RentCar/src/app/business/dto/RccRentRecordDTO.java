package app.business.dto;

import java.time.LocalDate;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
@EqualsAndHashCode(of = "idRecord")

public class RccRentRecordDTO {
	
	@PositiveOrZero
	private int idRecord;
	@NotNull
	private RccDriverDTO driver;
	@NotNull
	private RccCarDTO car;
	@NotNull
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate rentDate;
	@Min(1)
	private int rentDurationDays;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate returnDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")	
	private LocalDate endRepairDate;
	private double fuelInTank =-1.;
	private double damageRepairPrice =-1.;
	private double total = -1.;
	private boolean received = false;
	private boolean recordClosed = false;
	
}
