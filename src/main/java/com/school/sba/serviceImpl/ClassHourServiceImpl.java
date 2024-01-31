package com.school.sba.serviceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.ClassHour;
import com.school.sba.entity.Schedule;
import com.school.sba.enums.ClassStatus;
import com.school.sba.enums.UserRole;
import com.school.sba.exception.AcademicProgramNotExistsByIdException;
import com.school.sba.exception.ClasshourNotFoundByIdException;
import com.school.sba.exception.IllegalRequestException;
import com.school.sba.exception.ScheduleNotExistsException;
import com.school.sba.exception.SubjectNotFoundByIdException;
import com.school.sba.exception.UserNotFoundByIdException;
import com.school.sba.repoistory.AcademicProgramRepoistory;
import com.school.sba.repoistory.ClassHourRepoistory;
import com.school.sba.repoistory.ScheduleRepoistory;
import com.school.sba.repoistory.SubjectRepoistory;
import com.school.sba.repoistory.UserRepoistory;
import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.requestdto.ClassHourUpdateRequest;
import com.school.sba.responsedto.ClassHourResponses;
import com.school.sba.service.ClassHourService;
import com.school.sba.utlity.ResponseStructure;

@Service
public class ClassHourServiceImpl implements ClassHourService {

	@Autowired
	private ClassHourRepoistory classHourRepoistory;

	@Autowired
	private AcademicProgramRepoistory academicProgramRepoistory;

	@Autowired
	private ScheduleRepoistory scheduleRepoistory;

	@Autowired
	private UserRepoistory userRepoistory;

	@Autowired
	private SubjectRepoistory subjectRepoistory;


	@Autowired
	private ResponseStructure<String> structure;



	public ClassHour mapToClassHourRequest(ClassHourRequest classHourRequest) {
		return ClassHour.builder()
				.roomNo(classHourRequest.getRoomNo())
				.classStatus(classHourRequest.getClassStatus())
				.build();
	}

	//	public ClassHourResponses mapToClassHourResponses(ClassHour classHour) {
	//		return ClassHourResponses.builder().
	//				classHourId(classHour.getClassHourId())
	//				.beginsAt(classHour.getBeginsAt())
	//				.endsAt(classHour.getEndsAt())
	//				.classStatus(classHour.getClassStatus())
	////				.academics(classHour.getAcademicProgram().getProgramName())
	//				.build();
	//		
	//	}

	public ClassHourResponses ClassHourResponses(ClassHour classhour) {
		return ClassHourResponses.builder()
				.classHourId(classhour.getClassHourId())
				.beginsAt(classhour.getBeginsAt())
				.endsAt(classhour.getEndsAt())
				.classStatus(classhour.getClassStatus())
				.build();
	}

	private LocalDateTime dateToDateTime(LocalDate date,LocalTime time) {
		return LocalDateTime.of(date, time);
	}

	@Override
	public ResponseEntity<ResponseStructure<String>> addClassHourToacdemicprogram(int programId,ClassHourRequest classHourRequest) {

		return academicProgramRepoistory.findById(programId)
				.map(program ->{
					Schedule schedule = program.getSchool().getSchedule();

					if(schedule == null) { throw new ScheduleNotExistsException("Failed to GENERATE Class Hour"); }

					if(program.getClassHours()==null || program.getClassHours().isEmpty())
					{
						List<ClassHour> perDayClasshour = new ArrayList<ClassHour>();
						LocalDate date = program.getBeginsAt();

						// for generating day
						for(int day=1; day<=6; day++) { 
							LocalTime currentTime = schedule.getOpensAt();
							LocalDateTime lasthour = null;

							// for generating class hours per day
							for(int entry=1; entry<=schedule.getClassHoursPerDay(); entry++) { 
								ClassHour classhour = new ClassHour();

								if(currentTime.equals(schedule.getOpensAt())) { // first class hour of the day
									classhour.setBeginsAt(dateToDateTime(date,currentTime));
								}
								else if(currentTime.equals(schedule.getBreakTime())) {  // after break time
									lasthour = lasthour.plus(schedule.getBreakLengthInMinutes());
									classhour.setBeginsAt(dateToDateTime(date, lasthour.toLocalTime()));
								}
								else if(currentTime.equals(schedule.getLunchTime())) {  // after lunch time
									lasthour = lasthour.plus(schedule.getLunchLengthInMinutes());
									classhour.setBeginsAt(dateToDateTime(date, lasthour.toLocalTime()));
								}
								else { // rest class hours of that day
									classhour.setBeginsAt(dateToDateTime(date, lasthour.toLocalTime()));
								}
								classhour.setEndsAt(classhour.getBeginsAt().plus(schedule.getClassHourLengthInMinutes()));
								classhour.setClassStatus(ClassStatus.NOTSCHEDULED);
								classhour.setAcademicProgram(program);

								perDayClasshour.add(classHourRepoistory.save(classhour));

								lasthour = perDayClasshour.get(entry-1).getEndsAt();

								currentTime = lasthour.toLocalTime();

								if(currentTime.equals(schedule.getClosesAt())) // school closing time
									break;

							}
							date = date.plusDays(1);
						}
						program.setClassHours(perDayClasshour);
						academicProgramRepoistory.save(program);

						structure.setStatus(HttpStatus.CREATED.value());
						structure.setMessage("Classhour GENERATED for Program: "+program.getProgramName());
						structure.setData("Completed Successfully");

						return new ResponseEntity<ResponseStructure<String>> (structure,HttpStatus.CREATED);
					}
					else
						throw new IllegalRequestException("Classhours Already Generated for :: "+program.getProgramName()+" of ID: "+program.getProgramId());

				})
				.orElseThrow(() -> new AcademicProgramNotExistsByIdException("Failed to GENERATE Class Hour"));

	}

	@Override
	public ResponseEntity<ResponseStructure<List<ClassHourResponses>>> updateClasshours(
			List<ClassHourUpdateRequest> classhourrequests) {

		List<ClassHourResponses> updatedClassHourResponses=new ArrayList<>();
		ResponseStructure<List<com.school.sba.responsedto.ClassHourResponses>> structure=new ResponseStructure<>();

		for(ClassHourUpdateRequest classHourUpdateRequest :classhourrequests) {

			return classHourRepoistory.findById(classHourUpdateRequest.getClassHourId()).map(classhour->{

				return userRepoistory.findById(classHourUpdateRequest.getUserId()).map(user-> {

					return subjectRepoistory.findById(classHourUpdateRequest.getSubjectId()).map(subject->{

						if(user.getUserRole().equals(UserRole.TEACHER)&&user.getSubject().equals(subject))
						{						
							boolean isPresent=classHourRepoistory.existsByBeginsAtBetweenAndRoomNo(classhour.getBeginsAt(), classhour.getEndsAt(), classhour.getRoomNo());

							if(isPresent) {
								throw new IllegalRequestException("classRoom already enganged");
							}
							else {
								classhour.setUser(user);
								classhour.setSubject(subject);
								classhour.setRoomNo(classhour.getRoomNo());
								ClassHour save = classHourRepoistory.save(classhour);

								updatedClassHourResponses.add(ClassHourResponses(classhour));

								structure.setStatus(HttpStatus.CREATED.value());
								structure.setMessage("updated Sucessfully");
								structure.setData(updatedClassHourResponses);

								return new ResponseEntity<ResponseStructure<List<ClassHourResponses>>>(structure,HttpStatus.FOUND);
							}

						}else {
							throw new  IllegalRequestException("classRoom already enganged");
						}
					}).orElseThrow(()->new SubjectNotFoundByIdException("subject Not Found For given Id"));

				}).orElseThrow(()-> new UserNotFoundByIdException("User not Found for given Id"));

			}).orElseThrow(()->new ClasshourNotFoundByIdException("ClassHour Not FOund for given Id"));
		}
		return null;

	}

	private ClassHour deleteClasshour(ClassHour classHour) {
		classHourRepoistory.delete(classHour);
		return classHour;
	}
}