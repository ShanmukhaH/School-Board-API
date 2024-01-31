package com.school.sba.utlity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.school.sba.service.AcademicProgramService;
import com.school.sba.service.SchoolService;
import com.school.sba.service.UserService;

@Component
public class ScheduleJobs {

	@Autowired
	private UserService userService;

	@Autowired
	private AcademicProgramService academicProgramService;
	
	@Autowired
	private SchoolService schoolService;

	

	@Scheduled(fixedDelay = 1000l*60)
	public void test(){
		System.out.println("Schedule Jobs");
	}

	@Scheduled(fixedDelay = 1000l*60)
	public void deleteUserPermentaly() {
		userService.deleteUserPermentaly();
	}
	
	@Scheduled(fixedDelay = 1000l*60)
	public void deleteAcademicProgramPetmentaly() {
        academicProgramService.deleteAcademicProgramPermentaly();
	}
	
	@Scheduled(fixedDelay = 1000l*60)
	public void deleteSchoolPermentaly() {
		schoolService.deleteSchoolPermentaly();
	}
}