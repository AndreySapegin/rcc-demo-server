package app.business.entities;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
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
@EqualsAndHashCode(of ="idDriver")
@ToString(exclude = "records")
@Entity
@Table(name = "rcDriver")
public class Driver {

	@Id
	@Column(length = 20)
	private String idDriver;
	private LocalDate birthDay;
	private String phoneNumber;
	private String eMail;
	
	@OneToMany(mappedBy = "driver", orphanRemoval = true)
	private Set<RentRecord>  records = new HashSet<>();

	public Driver(String idDriver, LocalDate birthDay, String phoneNumber, String eMail) {
		super();
		this.idDriver = idDriver;
		this.birthDay = birthDay;
		this.phoneNumber = phoneNumber;
		this.eMail = eMail;
	}
	
	
}
