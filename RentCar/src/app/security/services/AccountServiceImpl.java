package app.security.services;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import app.security.entities.Account;
import app.security.repositories.AccountMongoRepository;

@Service
public class AccountServiceImpl implements AccountService {

	private static final String ROLE_PREF = "ROLE_";
	@Autowired AccountMongoRepository accRepo;
	@Autowired BCryptPasswordEncoder pasEncoder;
	
	@Override
	@Transactional
	public Account addAcount(String user, String password, Set<String> role) {
		accRepo.findById(user).ifPresent((u)-> {throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("add-acc: User $s already exist", user));});
		var account = new Account(user, pasEncoder.encode(password),
				role.stream().map( r -> ROLE_PREF + r).collect(Collectors.toSet()));
		return accRepo.save(account);
	}

	@Override
	public Account getAccount(String user) {
		return accRepo.findById(user).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("get-acc: User $s not exist", user)));
	}

	@Override
	@Transactional
	public Boolean deleteAccount(String user) {
		accRepo.findById(user).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("del-acc: User $s not exist", user)));
		accRepo.deleteById(user);
		return !accRepo.existsById(user);
	}

	@Override
	public List<Account> getAllAccounts() {
		return accRepo.findAll();
	}

	@Override
	@Transactional
	public Account grandRole(String user, String role) {
		var account = accRepo.findById(user).orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST,String.format("add-role: User $s not exist", user)));
		account.getRoles().add(ROLE_PREF+role);
		return accRepo.save(account);
	}

	@Override
	public Account depiveRole(String user, String role) {
		var account = accRepo.findById(user).orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST,String.format("del-role: User $s not exist", user)));
		account.setRoles(account.getRoles().stream().filter(r -> !r.equalsIgnoreCase(ROLE_PREF+role)).collect(Collectors.toSet()));
		return accRepo.save(account);
	}

	@Override
	public Account setPassword(String user, String password) {
		var account = accRepo.findById(user).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("chg-pswd: User $s not exist", user)));
		account.setPassword(pasEncoder.encode(password));
		return accRepo.save(account);
	}

}
