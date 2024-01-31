package com.school.sba.requestdto;

import com.school.sba.enums.ClassStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassHourRequest {
	
	private int roomNo;
	private ClassStatus classStatus;
}
