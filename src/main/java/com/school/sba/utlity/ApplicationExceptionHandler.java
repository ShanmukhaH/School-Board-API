package com.school.sba.utlity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.school.sba.exception.AcademicProgramNotFoundById;
import com.school.sba.exception.AdminCannotAssiginToAcademicProgram;
import com.school.sba.exception.AdminiNotFoundByUserRoleException;
import com.school.sba.exception.DuplicateEntryException;
import com.school.sba.exception.InvalidUserException;
import com.school.sba.exception.ScheduleIsAlreadyPresentException;
import com.school.sba.exception.ScheduleNotFoundByIdException;
import com.school.sba.exception.SchoolAlreadyPresentException;
import com.school.sba.exception.SchoolNotFoundByIDException;
import com.school.sba.exception.SubjectNotFoundByIdException;
import com.school.sba.exception.UserNotFoundByIdException;

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
	
	@ExceptionHandler(UserNotFoundByIdException.class)
	public ResponseEntity<Object> handlerUserNotFoundById(UserNotFoundByIdException ex){
		return structure(HttpStatus.OK, ex.getMessage(), "UserNotFound");
	}
	
	@ExceptionHandler(AdminiNotFoundByUserRoleException.class)
	public ResponseEntity<Object> handleAdminNotFound(AdminiNotFoundByUserRoleException ex){
		return structure(HttpStatus.BAD_REQUEST, ex.getMessage(), "Admin Not Found");
	}
	
	@ExceptionHandler(SchoolAlreadyPresentException.class)
	public ResponseEntity<Object> handleSchoolAlreadyPresent(SchoolAlreadyPresentException ex){
		return structure(HttpStatus.FOUND, ex.getMessage(), "School is Already Present");
	}
	
	@ExceptionHandler(SchoolNotFoundByIDException.class)
	public ResponseEntity<Object> handleSchoolNotFound(SchoolNotFoundByIDException ex){
		return structure(HttpStatus.NOT_FOUND, ex.getMessage(), "School Not Found");
	}
	
	@ExceptionHandler(ScheduleIsAlreadyPresentException.class)
	public ResponseEntity<Object> handleScheduleAlredyPresent(ScheduleIsAlreadyPresentException ex){
		return structure(HttpStatus.FOUND, ex.getMessage(), "Already School is Scheduled");
	}
	
	@ExceptionHandler(ScheduleNotFoundByIdException.class)
	public ResponseEntity<Object> handleSchduleNotFoundByid(ScheduleNotFoundByIdException ex){
		return structure(HttpStatus.NOT_FOUND, ex.getMessage(), "School-Schedule Not Found");
	}
	
	@ExceptionHandler(AcademicProgramNotFoundById.class)
	public ResponseEntity<Object> handleAcademicProgramNotFound(AcademicProgramNotFoundById ex){
		return structure(HttpStatus.NOT_FOUND, ex.getMessage(), "Academic Program Not Found");
	}
	
	@ExceptionHandler(AdminCannotAssiginToAcademicProgram.class)
	public ResponseEntity<Object> handleAdaminiCannotAssign(AdminCannotAssiginToAcademicProgram ex){
		return structure(HttpStatus.BAD_REQUEST, ex.getMessage(), " Not able to assign To programs");
	}
	
	@ExceptionHandler(SubjectNotFoundByIdException.class)
	public ResponseEntity<Object> handleSubjectNotFoundByid(SubjectNotFoundByIdException ex){
		return structure(HttpStatus.NOT_FOUND, ex.getMessage(), "Subjects not Found");
	}
	
	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<Object> handleUserNotFoundException(UsernameNotFoundException ex){
		return structure(HttpStatus.FOUND, ex.getMessage(), "Authenticate to load the User");
	}

}
