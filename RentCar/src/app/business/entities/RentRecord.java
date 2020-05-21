package app.business.entities;

import java.time.LocalDate;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@EqualsAndHashCode(of = "idRecord")
@Entity
@Table(name ="rcRecord")
public class RentRecord {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idRecord;
	
	@ManyToOne
	private Driver driver;
	
	@ManyToOne
	private Car car;
	private LocalDate rentDate;
	private int rentDurationDays;
	private LocalDate returnDate;
	private LocalDate endRepairDate;
	private double fuelInTank =-1.;
	private double damageRepairPrice =-1.;
	
	private double total = -1.;
	private boolean received = false;
	private boolean recordClosed = false;
	
	public RentRecord(Driver driver, Car car, LocalDate rentDate, int rentDurationDays) {
		super();
		this.driver = driver;
		this.car = car;
		this.rentDate = rentDate;
		this.rentDurationDays = rentDurationDays;
	}
	
	

}
