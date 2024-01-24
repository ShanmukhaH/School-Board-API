package com.school.sba.controller;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.requestdto.AcademicProgramRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.service.AcademicProgramService;
import com.school.sba.utlity.ResponseStructure;

@RestController
public class AcademicProgramController {

	@Autowired
    private AcademicProgramService academicProgramService;
	
	@PostMapping("/schools/{schoolId}/academic-programs")
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> createAcademicProgram(@RequestBody AcademicProgramRequest programRequest,@PathVariable int schoolId){
		return academicProgramService.createAcademicProgram(programRequest,schoolId);
	}
	
	@GetMapping("/schools/{schoolId}/academic-programs")
	public ResponseEntity<ResponseStructure<List<AcademicProgramResponse>>> findAllAcademicProgram(@PathVariable int schoolId){
		return academicProgramService.findAllAcademicProgram(schoolId);
	}
	
	@PutMapping("/academic-programs/{programId}/users/{userId}")
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> addUsersToProgram(@PathVariable int programId,@PathVariable int userId){
		return academicProgramService.addUsersToprogram(programId,userId);
	}
	
}
