package com.school.sba.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.school.sba.entity.ClassHour;
import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.requestdto.ClassHourUpdateRequest;
import com.school.sba.requestdto.ExcelRequestDto;
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

	// multipart file for standalone application
	@PostMapping("/academic-program/{programId}/class-hours/write-excel")
	public ResponseEntity<ResponseStructure<String>> writeIntoXLSheet(@PathVariable int programId,@RequestBody ExcelRequestDto excelRequest)
	{
		return classHourService.writeIntoXlSheet(programId,excelRequest);

	}
	
	@PostMapping("/academic-program/{programId}/class-hours/from/{fromDate}/to/{toDate}/write-excel")
	public ResponseEntity<?> writeToXLSheet(@PathVariable int programId,@PathVariable LocalDate fromDate,@PathVariable LocalDate toDate,@RequestParam MultipartFile multipartFile)
	{
		return classHourService.writeToXlSheet(programId,fromDate,toDate,multipartFile);
		
	}

}
