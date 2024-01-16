package com.school.sba.service;

import org.springframework.http.ResponseEntity;

import com.school.sba.entity.School;
import com.school.sba.utlity.ResponseStructure;

public interface SchoolService {
	
	public ResponseEntity<ResponseStructure<School>> addSchool(School school);
	public ResponseEntity<ResponseStructure<School>> findSchoolById(School school);
	public ResponseEntity<ResponseStructure<School>> findAllSchool();
	public ResponseEntity<ResponseStructure<School>> updateSchoolById(int schoolId);
	public ResponseEntity<ResponseStructure<School>> deleteSchoolById(int schoolId);
}
