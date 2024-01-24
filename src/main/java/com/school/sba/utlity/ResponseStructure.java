package com.school.sba.utlity;

import org.springframework.stereotype.Component;

import com.school.sba.responsedto.SchoolResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseStructure<T> {

	private int status;
	private String message;
	private T data;
}
