package com.school.sba.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AcademicProgramNotExistsByIdException extends RuntimeException {

	private String message;
}
