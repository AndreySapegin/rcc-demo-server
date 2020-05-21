package app.security.services;

import java.util.List;
import java.util.Set;

import app.security.entities.Account;

public interface AccountService {

	Account addAcount(String user, String password, Set<String> role); 
	Account getAccount(String user);
	Boolean deleteAccount(String user);
	List<Account> getAllAccounts();
	Account grandRole(String user, String role);
	Account depiveRole(String user, String role);
	Account setPassword(String user, String password);
	
}