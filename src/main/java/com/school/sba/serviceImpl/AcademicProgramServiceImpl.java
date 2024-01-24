package com.school.sba.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.School;
import com.school.sba.entity.Subject;
import com.school.sba.entity.User;
import com.school.sba.enums.UserRole;
import com.school.sba.exception.AcademicProgramNotFoundById;
import com.school.sba.exception.AdminCannotAssiginToAcademicProgram;
import com.school.sba.exception.SchoolNotFoundByIDException;
import com.school.sba.exception.UserNotFoundByIdException;
import com.school.sba.repoistory.AcademicProgramRepoistory;
import com.school.sba.repoistory.SchoolRepoistory;
import com.school.sba.repoistory.UserRepoistory;
import com.school.sba.requestdto.AcademicProgramRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.service.AcademicProgramService;
import com.school.sba.utlity.ResponseStructure;

@Service
public class AcademicProgramServiceImpl implements AcademicProgramService {

	@Autowired
	private AcademicProgramRepoistory academicProgramRepoistory;
	
	@Autowired
	private SchoolRepoistory schoolRepoistory;
	
	@Autowired
	private UserRepoistory userRepoistory;
	
	@Autowired
	private ResponseStructure<AcademicProgramResponse> structure;
	
	
	public static AcademicProgramResponse mapToAcademicProgramResponse(AcademicProgram academicProgram) {
		List<String> subjects=new ArrayList<>();
		for (Subject  subject : academicProgram.getSubjects()) {
			subjects.add(subject.getSubjectName());
		}
		return AcademicProgramResponse.builder()
				.programId(academicProgram.getProgramId())
				.programType(academicProgram.getProgramType())
				.programName(academicProgram.getProgramName())
				.beginsAt(academicProgram.getBeginsAt())
				.endsAt(academicProgram.getEndsAt())
				.subjects(subjects).build();
	}
	
	private AcademicProgram mapToAcademicProgram(AcademicProgramRequest academicProgramRequest) {
		return AcademicProgram.builder()
				.programType(academicProgramRequest.getProgramType())
				.programName(academicProgramRequest.getProgramName())
				.beginsAt(academicProgramRequest.getBeginsAt())
				.endsAt(academicProgramRequest
				.getEndsAt()).build();
	}

	
	@Override
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> createAcademicProgram(
			AcademicProgramRequest programRequest, int schoolId) {
		
		return schoolRepoistory.findById(schoolId).map(school -> {
			AcademicProgram academicProgram = mapToAcademicProgram(programRequest);
			academicProgram.setSchool(school);
			academicProgram  = academicProgramRepoistory.save(academicProgram);
			
			schoolRepoistory.save(school);
			structure.setStatus(HttpStatus.CREATED.value());
			structure.setMessage("Academic program created Sucesfully!");
			structure.setData(mapToAcademicProgramResponse(academicProgram));
            
			return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(structure,HttpStatus.CREATED);
			
		}).orElseThrow(() -> new SchoolNotFoundByIDException("School not found by given Id"));

	}

	@Override
	public ResponseEntity<ResponseStructure<List<AcademicProgramResponse>>> findAllAcademicProgram(int schoolId) {
		return schoolRepoistory.findById(schoolId).map(school ->{
			List<AcademicProgram> academicprogram=school.getProgramlist();
			ResponseStructure<List<AcademicProgramResponse>> respone=new ResponseStructure<>();
			
			List<AcademicProgramResponse> list=new ArrayList<>();
			for(AcademicProgram obj:academicprogram) {
			list.add(mapToAcademicProgramResponse(obj));
			}
			
			respone.setStatus(HttpStatus.OK.value());
			respone.setMessage("Data found Sucessfully");
			respone.setData(list);
			
			return new ResponseEntity<ResponseStructure<List<AcademicProgramResponse>>>(respone,HttpStatus.FOUND);
			
		}).orElseThrow(()->new SchoolNotFoundByIDException("invalid School Id"));
		
	}

	@Override
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> addUsersToprogram(int programId, int userId) {
		
		User user = userRepoistory.findById(userId)
				.orElseThrow(()-> new UserNotFoundByIdException("user not found for given ID"));

		AcademicProgram academicProgram = academicProgramRepoistory.findById(programId)
				.orElseThrow(()->new AcademicProgramNotFoundById("AcademicProgam not found"));

		if(user.getUserRole().equals(UserRole.ADMIN))
		{
			throw new AdminCannotAssiginToAcademicProgram("admine cannot assign");
		}
		else
		{
			user.getPrograms().add(academicProgram);
			userRepoistory.save(user);
			academicProgram.getUsers().add(user);
			academicProgramRepoistory.save(academicProgram );

			structure.setStatus(HttpStatus.OK.value());
			structure.setMessage("updated successfully");
			structure.setData(mapToAcademicProgramResponse(academicProgram));


			return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(structure,HttpStatus.OK);
		}
		
	}

	
}
