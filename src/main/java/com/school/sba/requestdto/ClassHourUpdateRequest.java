package com.school.sba.requestdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassHourUpdateRequest {

	private int classHourId;
	private int subjectId;
	private int userId;
	private int roomNo;
}
