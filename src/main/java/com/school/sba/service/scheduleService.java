package com.school.sba.service;

import org.springframework.http.ResponseEntity;

import com.school.sba.requestdto.ScheduleRequest;
import com.school.sba.responsedto.ScheduleResponse;
import com.school.sba.utlity.ResponseStructure;

public interface scheduleService {

	ResponseEntity<ResponseStructure<ScheduleResponse>> createSchedule(ScheduleRequest schedulerequest, int schoolId);

	ResponseEntity<ResponseStructure<ScheduleResponse>> findScheduleByschoolId(int schoolId);

	ResponseEntity<ResponseStructure<ScheduleResponse>> updateById(int scheduleId, ScheduleRequest scheduleRequest);

	
	

}
