package com.school.sba.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ScheduleNotExistsException extends RuntimeException {

	private String message;
}
