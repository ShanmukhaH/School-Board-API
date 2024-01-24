package com.school.sba.requestdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SchoolRequest {

	private String schoolName;
	private long contanctNo;
	private String emaild;
	private String adress;
}
