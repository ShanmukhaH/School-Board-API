package com.school.sba.responsedto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubjectResponse {

	private int subjectId;
	private String subjectName;
	
}
