package app.security.entities;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(of = "user")

@Document("account")
public class Account {

	@Id
	String user;
	String password;
	
	Set<String> roles = new HashSet<>();
	
	public Account(String user, String password, String role) {
		super();
		this.user = user;
		this.password= password;
		this.roles.add(role);
	}
}
