package com.school.sba.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.entity.ClassHour;
import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.requestdto.ClassHourUpdateRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.responsedto.ClassHourResponses;
import com.school.sba.service.ClassHourService;
import com.school.sba.utlity.ResponseStructure;

@RestController
public class ClassHourController {

	@Autowired
	private ClassHourService classHourService;
	
	
	@PostMapping("/academic-program/{programId}/class-hours")
	public ResponseEntity<ResponseStructure<String>> addClassHourToacdemicProgram(@PathVariable int programId,@RequestBody ClassHourRequest classHourRequest){
		return classHourService.addClassHourToacdemicprogram(programId,classHourRequest);
	}
	
	@PutMapping("/class-hours")
	public ResponseEntity<ResponseStructure<List<ClassHourResponses>>> updateClasshours(@RequestBody List<ClassHourUpdateRequest> classhourrequests){
		return classHourService.updateClasshours(classhourrequests);
	}
}
