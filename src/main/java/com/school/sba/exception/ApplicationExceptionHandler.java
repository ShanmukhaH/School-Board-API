package com.school.sba.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.school.sba.utlity.ErrorSturcture;

@RestControllerAdvice
public class ApplicationExceptionHandler {

	@Autowired
	private ErrorSturcture<String> str;
	
	@ExceptionHandler(SchoolNotFoundByIdException.class)
	public ResponseEntity<ErrorSturcture<String>> handlesSchoolNotFoundById(SchoolNotFoundByIdException exception){
		str.setStatus(HttpStatus.FOUND.value());
		str.setMessage(exception.getMessage());
		str.setRootcause("The Requested School with id is NotFound");
		
		return new ResponseEntity<ErrorSturcture<String>>(str,HttpStatus.FOUND);
	}
}
