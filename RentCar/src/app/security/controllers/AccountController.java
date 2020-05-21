package app.security.controllers;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.security.dto.AccountDTO;
import app.security.dto.AccountInputDTO;
import app.security.dto.HeaderDTO;
import app.security.dto.SecurityServiceDTO;
import app.security.entities.Account;
import app.security.jwt.JWTTokenUtil;
import app.security.services.AccountService;

@RestController
@RequestMapping("/")
public class AccountController {
	
	@Autowired AccountService service;
	@Autowired SecurityServiceDTO convert;
	@Autowired JWTTokenUtil jwt;
	
	@GetMapping("/login")
	public HeaderDTO getHeader(Authentication authentication) {
		return new HeaderDTO(jwt.generateToken(authentication));
	}
	@PostMapping("/admin/add_acc")
	public AccountDTO addAcount(@RequestBody AccountInputDTO  account) {
		return convert.getAccountDTO(service.addAcount(account.getUser(), account.getPassword(), account.getRoles()));
		} 
	
	@PostMapping("/admin/chg_pass")
	public AccountDTO cangePassword(String user, String password) {
		return convert.getAccountDTO(service.setPassword(user, password));
		} 
	
	@GetMapping("/admin/gt_acc")
	public AccountDTO getAccount(String user) {
		return convert.getAccountDTO(service.getAccount(user));
	}
	@GetMapping("/admin/dl_acc")
	public Boolean deleteAccount(String user) {
		return service.deleteAccount(user);
	}
	
	@GetMapping("/admin/gt_all_acc")
	public List<AccountDTO> getAllAccounts(){
		return service.getAllAccounts().stream().map(convert::getAccountDTO).collect(Collectors.toList());
	}
	
	@GetMapping("/admin/grnd_role")
	public AccountDTO grandRole(String user, String role) {
		return convert.getAccountDTO(service.grandRole(user, role));
	}
	@GetMapping("/admin/dpv_role")
	public AccountDTO depiveRole(String user, String role) {
		return convert.getAccountDTO(service.depiveRole(user, role));
	}
}
