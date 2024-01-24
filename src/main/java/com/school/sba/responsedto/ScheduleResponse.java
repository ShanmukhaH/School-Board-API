package com.school.sba.responsedto;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResponse {
	
	private int scheduleId;
	private LocalTime opensAt;
	private LocalTime closesAt;
	private int classHoursPerDay;
	private int classHourLengthInMinute;
	private LocalTime breakTime;
	private int breakLengthInMinute;
	private LocalTime lunchTime;
	private int lunchLengthInMinute;
}
