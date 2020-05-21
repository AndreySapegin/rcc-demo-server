package app.business.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor

@Data
@EqualsAndHashCode(of ="idModel")
@ToString(exclude = "cars")
@Entity
@Table(name = "rcModel")
public class Model {

	@Id
	@Column(length = 100)
	private String idModel;
	private double dailyRate = -1;
	private double tankVolume = -1;
	
	@OneToMany(mappedBy = "model")
	private Set<Car> cars = new HashSet<>(); 
	
	public Model(String idModel) {
		super();
		this.idModel = idModel;
	}

	public Model(String idModel, double dailyRate, double tankVolume) {
		super();
		this.idModel = idModel;
		this.dailyRate = dailyRate;
		this.tankVolume = tankVolume;
	}
	
	
}
