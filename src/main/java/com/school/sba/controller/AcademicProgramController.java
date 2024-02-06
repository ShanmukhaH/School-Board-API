	package com.school.sba.controller;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.enums.UserRole;
import com.school.sba.requestdto.AcademicProgramRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.responsedto.UserResponse;
import com.school.sba.service.AcademicProgramService;
import com.school.sba.utlity.ResponseStructure;

@RestController
public class AcademicProgramController {

	@Autowired
    private AcademicProgramService academicProgramService;
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/schools/{schoolId}/academic-programs")
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> createAcademicProgram(@RequestBody AcademicProgramRequest programRequest,@PathVariable int schoolId){
		return academicProgramService.createAcademicProgram(programRequest,schoolId);
	}
	
	@GetMapping("/schools/{schoolId}/academic-programs")
	public ResponseEntity<ResponseStructure<List<AcademicProgramResponse>>> findAllAcademicProgram(@PathVariable int schoolId){
		return academicProgramService.findAllAcademicProgram(schoolId);
	}
	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping("/academic-programs/{programId}/users/{userId}")
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> addUsersToProgram(@PathVariable int programId,@PathVariable int userId){
		return academicProgramService.addUsersToprogram(programId,userId);
	}
	
	@DeleteMapping("/academic-programs/{programId}")
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> deletePrograms(@PathVariable int programId){
		return academicProgramService.deletePrograms(programId);
	}
	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping("/academic-program/{programId}")
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> setAutoRepeatSchedule(@PathVariable int programId,
																			@RequestParam("auto-repeat-schedule") boolean autoRepeatSchedule){
		return academicProgramService.setAutoRepeatSchedule(programId,autoRepeatSchedule);
	}
 
}
