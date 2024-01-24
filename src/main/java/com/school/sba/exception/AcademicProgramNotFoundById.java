package com.school.sba.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AcademicProgramNotFoundById extends RuntimeException {
	private String message;
}
