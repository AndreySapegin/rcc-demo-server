package app.security.dto;

import org.springframework.stereotype.Service;

import app.security.entities.Account;

@Service
public class SecurityServiceDTO {

	public AccountDTO getAccountDTO(Account account) {
		return new AccountDTO(account.getUser(), account.getRoles());
	}
}
