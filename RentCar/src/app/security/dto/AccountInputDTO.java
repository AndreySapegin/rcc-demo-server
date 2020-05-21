package app.security.dto;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter

public class AccountInputDTO {

	private String user;
	private String password;
	private Set<String> roles = new HashSet<>();
}
