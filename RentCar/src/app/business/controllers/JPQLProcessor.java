package app.business.controllers;

import java.sql.SQLSyntaxErrorException;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;

import org.hibernate.QueryException;
import org.hibernate.hql.internal.ast.InvalidPathException;
import org.hibernate.hql.internal.ast.QuerySyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import antlr.NoViableAltException;
import antlr.SemanticException;


@RestController
@RequestMapping("/jpql")
@Validated
public class JPQLProcessor {

	@PersistenceContext EntityManager em;
	String template = "<HTML><HEADER>"
			+ "<TITLE>JPQL page</TITLE>"
			+ "</HEADER><BODY><FORM action=\"/jpql/submit\" method=\"get\"><TABLE width = \"600\">"
			+ "<TR><TD><input type=\"text\" id=\"str\" name =\"str\" value = \"#str\" size=\"100\"></TD>"
			+ "<TD><input type=\"submit\" value=\"Send\" ></TD></TR></TABLE></FORM>"
			+ "<DIV>#result</DIV>"
			+ "</BODY></HTML>";
	
	@SuppressWarnings("unchecked")
	@PostMapping("query")
	public String queryRequest(@NotBlank @RequestBody String str) {
		var emQuery = em.createQuery(str);
		var<?> rowQuery = emQuery.getResultList();
		String result= "- Empty -";
		if (rowQuery.size() == 0) return result;
		if (rowQuery.get(0).getClass().isArray()) {
			 result = (String) rowQuery.stream().map(r -> Arrays.deepToString((Object[]) r).replaceAll("[\\[\\]]", "")).collect(Collectors.joining("\n"));
		} else {
			 result = (String) rowQuery.stream().map(r -> r.toString()).collect(Collectors.joining("\n"));
		}
		return result;
	}
	
	@GetMapping()
	public String getPage() {
		var result = template.replace("#str", "").replace("#result", "");
		return result;
	}
	@GetMapping("submit")
	public String getRequest(String str) {
		var resp = queryRequest(str).replace("\n", "<BR>");
		var result = template.replace("#str", str).replace("#result", resp);
		return result;
	}
	
	  @ResponseStatus(HttpStatus.BAD_REQUEST)
	  @ResponseBody	  
	  @ExceptionHandler(value = {SQLSyntaxErrorException.class,
			  QueryException.class,
			  QuerySyntaxException.class,
			  InvalidPathException.class,
			  SemanticException.class,
			  NoViableAltException.class,
			  IllegalStateException.class})
	  public String
	  defaultErrorHandler(HttpServletRequest req, Exception e) {
		System.out.println("-------- Exception ------>>> " + e.getClass().getName());  
	    return e.getClass().getName() + "   " + e.getMessage();
	  }
}
