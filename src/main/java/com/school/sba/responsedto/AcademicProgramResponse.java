package com.school.sba.responsedto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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
public class AcademicProgramResponse {

	private int programId;
	private String programName;
	private LocalDate beginsAt;
	private LocalDate endsAt;
	private ProgramType programType;
	private List<String> subjects;
}
