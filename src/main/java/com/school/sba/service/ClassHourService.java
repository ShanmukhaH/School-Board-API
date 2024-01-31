package com.school.sba.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.requestdto.ClassHourUpdateRequest;
import com.school.sba.responsedto.ClassHourResponses;
import com.school.sba.utlity.ResponseStructure;

public interface ClassHourService {

	ResponseEntity<ResponseStructure<String>> addClassHourToacdemicprogram(int programId,ClassHourRequest classHourRequest);

	ResponseEntity<ResponseStructure<List<ClassHourResponses>>> updateClasshours(
			List<ClassHourUpdateRequest> classhourrequests);

   

}
