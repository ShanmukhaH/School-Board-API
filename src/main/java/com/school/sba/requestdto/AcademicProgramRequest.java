package com.school.sba.requestdto;

import java.time.LocalDate;

import com.school.sba.enums.ProgramType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcademicProgramRequest {

	private String programName;
	private LocalDate beginsAt;
	private LocalDate endsAt;
	private ProgramType programType;
}
