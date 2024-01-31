package com.school.sba.serviceImpl;

import java.time.Duration;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.Schedule;
import com.school.sba.entity.School;
import com.school.sba.exception.ScheduleIsAlreadyPresentException;
import com.school.sba.exception.ScheduleNotFoundByIdException;
import com.school.sba.exception.SchoolNotFoundByIDException;
import com.school.sba.repoistory.ScheduleRepoistory;
import com.school.sba.repoistory.SchoolRepoistory;
import com.school.sba.requestdto.ScheduleRequest;
import com.school.sba.responsedto.ScheduleResponse;
import com.school.sba.service.scheduleService;
import com.school.sba.utlity.ResponseStructure;


@Service
public class ScheduleServiceImpl implements scheduleService {


	@Autowired
	private ScheduleRepoistory scheduleRepoistory;

	@Autowired
	private SchoolRepoistory schoolRepoistory;

	@Autowired
	private ResponseStructure<ScheduleResponse> structure;


	public Schedule mapToScheduleRequest(ScheduleRequest scheduleRequest) {
		//		System.out.println(Duration.ofMinutes(scheduleRequest.getClassHourLengthInMinutes())+" "+Duration.ofMinutes(scheduleRequest.getClassHourLengthInMinutes()));
		return Schedule.builder()
				.opensAt(scheduleRequest.getOpensAt())
				.closesAt(scheduleRequest.getClosesAt())
				.classHoursPerDay(scheduleRequest.getClassHoursPerDay())
				.classHourLengthInMinutes(Duration.ofMinutes(scheduleRequest.getClassHourLengthInMinutes()))
				.breakTime(scheduleRequest.getBreakTime())
				.breakLengthInMinutes(Duration.ofMinutes(scheduleRequest.getBreakLengthInMinutes()))
				.lunchTime(scheduleRequest.getLunchTime())
				.lunchLengthInMinutes(Duration.ofMinutes(scheduleRequest.getLunchLengthInMinutes()))
				.build();

	}

	public ScheduleResponse mapToScheduleResponse(Schedule schedule) {
		//		System.out.println(schedule.getClassHourLengthInMinutes().toMinutes());
		return ScheduleResponse.builder()
				.scheduleId(schedule.getScheduleId())
				.opensAt(schedule.getOpensAt())
				.closesAt(schedule.getClosesAt())
				.classHoursPerDay(schedule.getClassHoursPerDay())
				.classHourLengthInMinute((int)schedule.getBreakLengthInMinutes().toMinutes())
				.breakTime(schedule.getBreakTime())
				.breakLengthInMinute((int)schedule.getBreakLengthInMinutes().toMinutes())
				.lunchTime(schedule.getLunchTime())
				.lunchLengthInMinute((int)schedule.getLunchLengthInMinutes().toMinutes())
				.build();
	}

	@Override
	public ResponseEntity<ResponseStructure<ScheduleResponse>> createSchedule(ScheduleRequest schedulerequest,
			int schoolId) {

		School school=schoolRepoistory.findById(schoolId).orElseThrow(()->new SchoolNotFoundByIDException("School is Not Found For given Request"));
		if(school.getSchedule()==null) {
			Schedule schedule = scheduleRepoistory.save(mapToScheduleRequest(schedulerequest));
			school.setSchedule(schedule);
			schoolRepoistory.save(school);

			structure.setStatus(HttpStatus.CREATED.value());
			structure.setMessage("School Schedule Updated Sucessfully!!");
			structure.setData(mapToScheduleResponse(schedule));
		}else {
			throw new ScheduleIsAlreadyPresentException("Already Schedule is Present");
		}

		return new ResponseEntity<ResponseStructure<ScheduleResponse>>(structure,HttpStatus.OK);
	}

	@Override
	public ResponseEntity<ResponseStructure<ScheduleResponse>> findScheduleByschoolId(int schoolId) {
		return schoolRepoistory.findById(schoolId).map(school -> {
			if (school.getSchedule() != null) {
				structure.setStatus(HttpStatus.FOUND.value());
				structure.setMessage("schedule data found");
				structure.setData(mapToScheduleResponse(school.getSchedule()));
				return new ResponseEntity<ResponseStructure<ScheduleResponse>>(structure, HttpStatus.FOUND);
			} else
				throw new ScheduleNotFoundByIdException("Schedule data Not Found By Id");

		}).orElseThrow(() -> new SchoolNotFoundByIDException("School Data not Found to given Id"));

	}

	@Override
	public ResponseEntity<ResponseStructure<ScheduleResponse>> updateById(int scheduleId,
			ScheduleRequest scheduleRequest) {

		return scheduleRepoistory.findById(scheduleId).map(schedule -> {
			Schedule schedule2 = mapToScheduleRequest(scheduleRequest);
			schedule2.setScheduleId(schedule.getScheduleId());
			schedule2 = scheduleRepoistory.save(schedule2);
			ScheduleResponse scheduleResponse = mapToScheduleResponse(schedule2);
			structure.setStatus(HttpStatus.OK.value());
			structure.setMessage("schedule data Updated Successfully");
			structure.setData(scheduleResponse);
			return new ResponseEntity<ResponseStructure<ScheduleResponse>>(structure, HttpStatus.OK);

		}).orElseThrow(() -> new ScheduleNotFoundByIdException("Schedule data Not Found By Id"));

	}

	private Schedule deleteSchedule(Schedule schedule) {
		scheduleRepoistory.delete(schedule);
		return schedule;
	}








}
