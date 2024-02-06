package com.school.sba.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.requestdto.ClassHourUpdateRequest;
import com.school.sba.requestdto.ExcelRequestDto;
import com.school.sba.responsedto.ClassHourResponses;
import com.school.sba.utlity.ResponseStructure;

public interface ClassHourService {

	ResponseEntity<ResponseStructure<String>> addClassHourToacdemicprogram(int programId,ClassHourRequest classHourRequest);

	ResponseEntity<ResponseStructure<List<ClassHourResponses>>> updateClasshours(
			List<ClassHourUpdateRequest> classhourrequests);

	ResponseEntity<ResponseStructure<String>> writeIntoXlSheet(int programId, ExcelRequestDto excelRequest);

	ResponseEntity<?> writeToXlSheet(int programId, LocalDate fromDate, LocalDate toDate, MultipartFile multipartFile);



   

}
