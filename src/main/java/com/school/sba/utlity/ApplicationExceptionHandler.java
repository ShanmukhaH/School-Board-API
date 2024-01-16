package com.school.sba.utlity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.school.sba.exception.DuplicateEntryException;
import com.school.sba.exception.InvalidUserException;
import com.school.sba.exception.UserNotFoundById;

@RestControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler{
	

	private ResponseEntity<Object> structure(HttpStatus status,String message,Object rootcause){
		return new ResponseEntity<Object>(Map.of(
				"status",status.value(),
				"message",message,
				"rootcause",rootcause),status
				);
	}
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		List<ObjectError> allErrors = ex.getAllErrors();
		
		Map<String, String> errors=new HashMap<String,String>();
		
		allErrors.forEach(error->{
			FieldError fieldError=(FieldError)error; 
			errors.put(fieldError.getField(), fieldError.getDefaultMessage());
		});
		
		return structure(HttpStatus.BAD_REQUEST, "Failed to Save the data", errors);
		 
		}
	
	@ExceptionHandler(InvalidUserException.class)
	public ResponseEntity<Object> handlerInvalidUserTypeException(InvalidUserException ex){
		return structure(HttpStatus.BAD_REQUEST, ex.getMessage(), "Invalid user");
	}
	
	@ExceptionHandler(DuplicateEntryException.class)
	public ResponseEntity<Object> handlerDuplicateEntryException(DuplicateEntryException ex){
		return structure(HttpStatus.BAD_REQUEST, ex.getMessage(), "Duplicate Entry");
	}
	
	public ResponseEntity<Object> handlerUserNotFoundById(UserNotFoundById ex){
		return structure(HttpStatus.OK, ex.getMessage(), "UserNotFound");
	}

}
