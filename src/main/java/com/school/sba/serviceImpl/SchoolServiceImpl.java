package com.school.sba.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.School;
import com.school.sba.repoistory.SchoolRepoistory;
import com.school.sba.service.SchoolService;
import com.school.sba.utlity.ResponseStructure;

@Service
public class SchoolServiceImpl implements SchoolService {

	@Autowired
	private SchoolRepoistory repoistory;
	
	@Autowired
	private ResponseStructure<School> str;
	
	@Override
	public ResponseEntity<ResponseStructure<School>> addSchool(School school) {
		
		School saveschool = repoistory.save(school);
		str.setStatus(HttpStatus.FOUND.value());
		str.setMessage("School created sucessfully!!");
		str.setData(saveschool);
		
		return new  ResponseEntity<ResponseStructure<School>>(str,HttpStatus.FOUND);
	}

	@Override
	public ResponseEntity<ResponseStructure<School>> findSchoolById(School school) {
		
		java.util.Optional<School> optional = repoistory.findById(school.getSchoolId());
		if(optional.isPresent()) {
			str.setStatus(HttpStatus.FOUND.value());
			str.setMessage("School Data is Found");
			str.setData(optional.get());
			
			return new ResponseEntity<ResponseStructure<School>>(str,HttpStatus.FOUND);
		}
		
		return null;
	}
	

	@Override
	public ResponseEntity<ResponseStructure<School>> findAllSchool() {
	
		List<School> list = repoistory.findAll();
		if(!list.isEmpty()) {
			str.setStatus(HttpStatus.FOUND.value());
			str.setMessage("List of School is Found");
			str.setData(list.getFirst());
			return new ResponseEntity<ResponseStructure<School>>(str,HttpStatus.FOUND);
		}
		
		return null;
	}

	@Override
	public ResponseEntity<ResponseStructure<School>> updateSchoolById(int schoolId) {
	
		Optional<School> optional = repoistory.findById(schoolId);
		if(optional.isPresent()) {
			
		}
		return null;
	}

	@Override
	public ResponseEntity<ResponseStructure<School>> deleteSchoolById(int schoolId) {
		// TODO Auto-generated method stub
		return null;
	}

	






}
