package com.school.sba.utlity;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseStructure<T> {

	private int status;
	private String message;
	private T data;
}
