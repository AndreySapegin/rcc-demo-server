package app.security;


import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import app.security.jwt.JWTFilter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{

	@Autowired AuthenticationEntryPoint entryPoint;
	@Autowired JWTFilter jwtFilter;
	
	@Bean 
	public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
		final Properties users = new Properties();	
		users.put("root","$2y$12$KDq5GCSr14Jzb6nEbNGeA.lNMmyf5OgtKeGLD1sO6Q3S3mnJZ9IhG,ROLE_ADMIN,enabled");
		return new InMemoryUserDetailsManager(users);
	}
	
	@Bean
	public UserDetailsService userDetailsService() {
		return new AuthentcationService();
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception{
		auth.userDetailsService(userDetailsService());
		auth.userDetailsService(inMemoryUserDetailsManager());
	}
	
	@Override 
	protected void configure(HttpSecurity http) throws Exception{
		http.csrf().disable()//.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).and()
		.exceptionHandling()
						.authenticationEntryPoint(entryPoint)
		.and()
		.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
		.cors()		
		.and()
		.httpBasic()
		.and()
		.authorizeRequests().antMatchers("/admin/**").hasAnyRole("ADMIN","BOSS")
			.antMatchers("/boss/**").hasAnyRole("BOSS")
			.antMatchers("/mng/**").hasAnyRole("BOSS","MANAGER")
			.antMatchers("/stf/**").hasAnyRole("CLERK","BOSS","MANAGER")
			.antMatchers("/atic/**").hasRole("ANALITIC")
			.antMatchers("/login").authenticated()
			.anyRequest().permitAll();
		
		
	}
			
}
