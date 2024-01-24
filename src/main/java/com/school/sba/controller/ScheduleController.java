package com.school.sba.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.requestdto.ScheduleRequest;
import com.school.sba.responsedto.ScheduleResponse;
import com.school.sba.service.scheduleService;
import com.school.sba.utlity.ResponseStructure;

@RestController
public class ScheduleController {

	@Autowired
	private scheduleService scheduleService;
	
	@PostMapping("/schools/{schoolId}/schedules")
	public ResponseEntity<ResponseStructure<ScheduleResponse>> createSchedule(@RequestBody ScheduleRequest schedulerequest,@PathVariable int schoolId){
		return scheduleService.createSchedule(schedulerequest,schoolId);
	}
	
	@GetMapping("/schools/{schoolId}/schedules")
	public ResponseEntity<ResponseStructure<ScheduleResponse>> findScheduleByschool(@PathVariable int schoolId){
		return scheduleService.findScheduleByschoolId(schoolId);
	}
	
	@PutMapping("/schedules/{scheduleId}")
	public ResponseEntity<ResponseStructure<ScheduleResponse>> updateById(@PathVariable int scheduleId,@RequestBody ScheduleRequest scheduleRequest){
		return scheduleService.updateById(scheduleId,scheduleRequest);
	}
	
}
