package app.business.controllers;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.springframework.core.Ordered;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice(basePackages = "app.controller")
public class CommonExceptionHandler   {

	  @ExceptionHandler(value = Exception.class)
	  public String
	  defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
		System.out.println("-------- Execption ------>>> " + e.getClass().getName());  
	    if (AnnotationUtils.findAnnotation
	                (e.getClass(), ResponseStatus.class) != null)
	      throw e;
	    return e.getMessage();
	  }
	  
	  @ResponseStatus(HttpStatus.BAD_REQUEST)
	  @ResponseBody	  
	  @ExceptionHandler(value = MethodArgumentNotValidException.class)
	  public List<String> 
	  	notValidException(HttpServletRequest req, MethodArgumentNotValidException e){
		  BindingResult bindingResult = e.getBindingResult();
		  List<String> result = new ArrayList<>();
		  result.addAll(bindingResult.getAllErrors().stream()
				  .map(err -> err.getDefaultMessage() + ":" + ((FieldError) err).getRejectedValue().toString())
				  .collect(Collectors.toList())
				  );
		  return result;
	  }
	  
	  @ResponseStatus(HttpStatus.BAD_REQUEST)
	  @ExceptionHandler(ConstraintViolationException.class)
	  @ResponseBody
	  public List<String>
	  	constraintViolation(HttpServletRequest req, ConstraintViolationException e){
		  List<String> result = new ArrayList<>();
		  result.addAll(e.getConstraintViolations().stream()
				  .map(err -> err.getMessage()+":"+err.getInvalidValue())
				  .collect(Collectors.toList())
				  );
		  return result;
	  }
	  
	  @ResponseStatus(HttpStatus.BAD_REQUEST)
	  @ExceptionHandler(HttpMessageNotReadableException.class)
	  @ResponseBody
	  public String httpMessageNotReadable(HttpServletRequest req, HttpMessageNotReadableException e) {
		  return e.getMessage();
	  }

	  @ResponseStatus(HttpStatus.BAD_REQUEST)
	  @ExceptionHandler(RuntimeException.class)
	  @ResponseBody
	  public String RuntimeExcrptionHandler(HttpServletRequest req, RuntimeException e) {
		  return e.getMessage();
	  }

}
