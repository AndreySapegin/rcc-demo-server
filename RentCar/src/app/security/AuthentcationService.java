package app.security;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


import app.security.repositories.AccountMongoRepository;

public class AuthentcationService implements UserDetailsService {
	@Autowired AccountMongoRepository accRepo;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		var account  = accRepo.findById(username).orElseThrow(()->new UsernameNotFoundException("User or password wrong"));
		return new User(account.getUser(),
				account.getPassword(),
				account.getRoles().parallelStream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
	}

}
