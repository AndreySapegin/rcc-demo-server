package app.business.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Data
@EqualsAndHashCode(of = "idCar")

@Entity
@Table(name = "rcCar")
public class Car {

	@Id
	@Column(length = 100)
	private String idCar;
	private boolean inUse = false;
	private boolean isWritenOff;
	
	@ManyToOne
	private Model model;

	public Car(String idCar) {
		super();
		this.idCar = idCar;
	}
	
	public Car(String idCar, boolean inUse, boolean isWritenOff, Model model) {
		super();
		this.idCar = idCar;
		this.inUse = inUse;
		this.isWritenOff = isWritenOff;
		this.model = model;
	}
}
