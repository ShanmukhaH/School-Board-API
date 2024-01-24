package com.school.sba.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SubjectNotFoundByIdException extends RuntimeException {
 private String message;
}
