package com.school.sba.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.Subject;
import com.school.sba.enums.UserRole;
import com.school.sba.exception.AcademicProgramNotExistsByIdException;
import com.school.sba.exception.AcademicProgramNotFoundById;
import com.school.sba.exception.IllegalRequestException;
import com.school.sba.exception.SchoolNotFoundByIDException;
import com.school.sba.exception.UserNotFoundByIdException;
import com.school.sba.repoistory.AcademicProgramRepoistory;
import com.school.sba.repoistory.ClassHourRepoistory;
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
	private UserServiceImpl userServiceImpl;
	
	@Autowired
	private ClassHourRepoistory classHourRepoistory;
	
	@Autowired
	private ResponseStructure<AcademicProgramResponse> structure;


	public static AcademicProgramResponse mapToAcademicProgramResponse(AcademicProgram academicProgram) {
		List<String> subjects=new ArrayList<>();
		if(academicProgram.getSubjects()!=null)
		{
			for (Subject  subject : academicProgram.getSubjects()) {
				subjects.add(subject.getSubjectName());
			}
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

		return userRepoistory.findById(userId)
				.map(user -> {
					AcademicProgram pro = null;
					if(user.getUserRole().equals(UserRole.ADMIN))
						throw new IllegalRequestException("Failed to SET user to THIS PROGRAM");
					else{
						pro = academicProgramRepoistory.findById(programId)
							.map(program -> {
								if(user.getUserRole().equals(UserRole.TEACHER)) {
									
									if(user.getSubject()==null){ 
										throw new IllegalRequestException("Teacher should assigned to a SUBJECT");}
									
									if(program.getSubjects()==null || program.getSubjects().isEmpty()){ 
										throw new IllegalRequestException("Program should assigned with SUBJECTS to Add TEACHER");}
									
									if(!program.getSubjects().contains(user.getSubject())){
										throw new IllegalRequestException("Irrelevant TEACHER to the Academic Program");
									}
								}
								
								user.getPrograms().add(program);
								userRepoistory.save(user);
								program.getUsers().add(user);
								program = academicProgramRepoistory.save(program);
								return program;
							}
							)
							.orElseThrow(() -> new AcademicProgramNotExistsByIdException("Failed to SET user to THIS PROGRAM"));
						}
					structure.setStatus(HttpStatus.OK.value());
					structure.setMessage(user.getUserRole()+" assigned with the Program "+pro.getProgramName());
					structure.setData(mapToAcademicProgramResponse(pro));
					
					return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(structure, HttpStatus.OK); 
				})
				.orElseThrow(()-> new UserNotFoundByIdException("Failed to SET user to THIS PROGRAM"));

	}

	@Override
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> deletePrograms(int programId) {
		 
		AcademicProgram program = academicProgramRepoistory.findById(programId).orElseThrow(()->new AcademicProgramNotFoundById("Academic Program not found for Given id"));
		if(program.isDeleted()==false)
			program.setDeleted(true);
		academicProgramRepoistory.save(program);
		
		structure.setStatus(HttpStatus.OK.value());
		structure.setMessage("Program Deleted Sucesfully!!!!!");
		structure.setData(mapToAcademicProgramResponse(program));

		return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(structure,HttpStatus.OK);
	}

	public void deleteAcademicProgramPermentaly() {
	
		List<AcademicProgram> programs = academicProgramRepoistory.findByisDeletedTrue();
		
		if(!programs.isEmpty()) {
		for(AcademicProgram program:programs) {
			classHourRepoistory.deleteAll(program.getClassHours());
			academicProgramRepoistory.delete(program);
		}
		}
	}
}


