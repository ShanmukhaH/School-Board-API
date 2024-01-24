package com.school.sba.responsedto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolResponse {

	private int schoolId;
	private String schoolName;
	private long contanctNo;
	private String emaild;
	private String adress;
}
