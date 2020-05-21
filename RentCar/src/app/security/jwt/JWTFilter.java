package app.security.jwt;



import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.core.type.TypeReference;


@Component
public class JWTFilter extends OncePerRequestFilter{

	@Autowired private JWTTokenUtil jwtTokenUtil;

	@SuppressWarnings({ "unchecked" })
	@Override
	protected void doFilterInternal(HttpServletRequest request,	HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
	
		String jwtToken = null;

		// check token
		String requestTokenHeader = request.getHeader("Authorization");
		if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
			jwtToken = requestTokenHeader.substring(7);
			try {
				var claims  = jwtTokenUtil.getAllClaimsFromToken(jwtToken);
				if (claims.getSubject() != null && SecurityContextHolder.getContext().getAuthentication() == null) {
					var role = ((List<String>) claims.get("roles", List.class))
							.stream()
							.map(SimpleGrantedAuthority::new)
							.collect(Collectors.toList());
					SecurityContextHolder.getContext()
						.setAuthentication(new UsernamePasswordAuthenticationToken(claims.getSubject(), 
								null, 
								role));
				}
			} catch (Exception e) {
				request.setAttribute("wrongToken", e.getMessage());
			} 
		}
		
		// go to next filter
		chain.doFilter(request, response);
	}
}
