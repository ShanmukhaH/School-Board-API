package com.school.sba.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SchoolAlreadyPresentException extends RuntimeException {

	private String message;
}
