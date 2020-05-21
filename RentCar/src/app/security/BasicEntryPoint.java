package app.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class BasicEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		var msg = (String) request.getAttribute("wrongToken");
		if (msg == null) {
			msg = authException.getMessage();
			response.addHeader("WWW-Authenticate", "Basic realm=/login");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
				String.format("HTTP Status 401 : Not authenticated. %s", msg));
	}

}
