package com.school.sba.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.Subject;
import com.school.sba.exception.AcademicProgramNotFoundById;
import com.school.sba.repoistory.AcademicProgramRepoistory;
import com.school.sba.repoistory.SubjectRepoistory;
import com.school.sba.requestdto.SubjectRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.responsedto.SubjectResponse;
import com.school.sba.service.SubjectService;
import com.school.sba.utlity.ResponseStructure;

@Service
public class SubjectServiceImpl implements SubjectService {

	@Autowired
	private SubjectRepoistory subjectRepoistory;

	@Autowired
	private AcademicProgramRepoistory academicProgramRepoistory;

	@Autowired
	private ResponseStructure<AcademicProgramResponse> structure;

	
	private SubjectResponse mapToSubjectResponse(Subject subject) {
		return SubjectResponse.builder()
				.subjectId(subject.getSubjectId())
				.subjectName(subject.getSubjectName())
				.build();
	}
	
	
	@Override
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> addSubject(SubjectRequest subjectRequest,int programId) {

		return academicProgramRepoistory.findById(programId).map(program ->{
			List<Subject> subjects=new ArrayList<Subject>();
			subjectRequest.getSubjectNames().forEach(name-> {

				Subject subject=subjectRepoistory.findBySubjectName(name).map(s->s).orElseGet(()->{
					Subject subject2=new Subject();
					subject2.setSubjectName(name);
					subjectRepoistory.save(subject2);
					return subject2;
				});
				subjects.add(subject);

			});
			program.setSubjects(subjects);
			academicProgramRepoistory.save(program);
			structure.setStatus(HttpStatus.CREATED.value());
			structure.setMessage("Updated the Subject list to Academic Program");
			structure.setData(AcademicProgramServiceImpl. mapToAcademicProgramResponse(program));
			return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(structure,HttpStatus.CREATED);

		}).orElseThrow(()->new AcademicProgramNotFoundById("Academic program Not found for given id"));

	}

	@Override
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> updateSubject(SubjectRequest subjectRequest,
			int programId) {

		
		return academicProgramRepoistory.findById(programId).map(program->{ //found academic program
			List<Subject>subjects= (program.getSubjects()!= null)?program.getSubjects(): new ArrayList<Subject>();

			//to add new subjects that are specified by the client
			subjectRequest.getSubjectNames().forEach(name->{
				boolean isPresent =false;
				for(Subject subject:subjects) {
					isPresent = (name.equalsIgnoreCase(subject.getSubjectName()))?true:false;
					if(isPresent)break;
				}
				if(!isPresent)subjects.add(subjectRepoistory.findBySubjectName(name)
						.orElseGet(()-> subjectRepoistory.save(Subject.builder().subjectName(name).build())));
			});
			//to remove the subjects that are not specified by the client
			List<Subject>toBeRemoved= new ArrayList<Subject>();
			subjects.forEach(subject->{
				boolean isPresent = false;
				for(String name:subjectRequest.getSubjectNames()) {
					isPresent=(subject.getSubjectName().equalsIgnoreCase(name))?true :false;
					if(!isPresent)break;
				}
				if(!isPresent)toBeRemoved.add(subject);
			});
			subjects.removeAll(toBeRemoved);


			program.setSubjects(subjects);//set subjects list to the academic program
			academicProgramRepoistory.save(program);//saving updated program to the database
			structure.setStatus(HttpStatus.CREATED.value());
			structure.setMessage("updated the subject list to academic program");
			structure.setData(AcademicProgramServiceImpl.mapToAcademicProgramResponse(program));
			return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(structure,HttpStatus.CREATED);
		}).orElseThrow(()-> new AcademicProgramNotFoundById("AcademicProgram not found"));

		
		
		
//		return academicProgramRepoistory.findById(programId).map(program -> {
//			program.getSubjects().clear();
//			academicProgramRepoistory.save(program);
//
//			return addSubject(subjectRequest, programId);
//		}).orElseThrow(()-> new AcademicProgramNotFoundById("Academic program Not found for given id"));
	}

	@Override
	public ResponseEntity<ResponseStructure<List<SubjectResponse>>> findAllSubjects() {
		
		List<Subject> subjects = subjectRepoistory.findAll();
		
		List<SubjectResponse> list=new ArrayList<SubjectResponse>();
		if(!subjects.isEmpty()) {
			for(Subject subject: subjects) {
				list.add(mapToSubjectResponse(subject));
			}
	        
		}
		ResponseStructure<List<SubjectResponse>> structure=new ResponseStructure<>();
		structure.setStatus(HttpStatus.FOUND.value());
		structure.setMessage("Subjects Data Found Sucessfully!");
		structure.setData(list);
		
		return new ResponseEntity<ResponseStructure<List<SubjectResponse>>>(structure,HttpStatus.FOUND);
	}
		
}

