package com.school.sba.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.entity.School;
import com.school.sba.service.SchoolService;
import com.school.sba.utlity.ResponseStructure;

@RestController
public class SchoolController {

	@Autowired
	private SchoolService schoolService;
	
	@PostMapping("/addschool")
	public ResponseEntity<ResponseStructure<School>> addSchool(@RequestBody School school){
		return schoolService.addSchool(school);
	}
	
	@GetMapping("/findSchool")
	public ResponseEntity<ResponseStructure<School>> findSchoolById(@RequestBody School school){
		return schoolService.findSchoolById(school);
	}
	@GetMapping("/AllSchool")
	public ResponseEntity<ResponseStructure<School>> findAllSchool(){
		return schoolService.findAllSchool();
	}
	
}
