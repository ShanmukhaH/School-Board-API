package com.school.sba.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SchoolNotFoundByIdException extends RuntimeException {

	private String message;
	
}
