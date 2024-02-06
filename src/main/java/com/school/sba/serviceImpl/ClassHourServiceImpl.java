package com.school.sba.serviceImpl;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.ClassHour;
import com.school.sba.entity.Schedule;
import com.school.sba.enums.ClassStatus;
import com.school.sba.enums.UserRole;
import com.school.sba.exception.AcademicProgramNotExistsByIdException;
import com.school.sba.exception.AcademicProgramNotFoundById;
import com.school.sba.exception.ClasshourNotFoundByIdException;
import com.school.sba.exception.DataNotExistException;
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
import com.school.sba.requestdto.ExcelRequestDto;
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

//		public ClassHourResponses mapToClassHourResponses(ClassHour classHour) {
//			return ClassHourResponses.builder().
//					classHourId(classHour.getClassHourId())
//					.beginsAt(classHour.getBeginsAt())
//					.endsAt(classHour.getEndsAt())
//					.classStatus(classHour.getClassStatus())
////					.academics(classHour.getAcademicProgram().getProgramName())
//					.build();
//			
//		}
	
	private ClassHour mapToNewClassHour(ClassHour existingClassHour) {
		return ClassHour.builder()
				.user(existingClassHour.getUser())
				.academicProgram(existingClassHour.getAcademicProgram())
				.roomNo(existingClassHour.getRoomNo())
				.beginsAt(existingClassHour.getBeginsAt().plusDays(7))
				.endsAt(existingClassHour.getEndsAt().plusDays(7))
				.classStatus(existingClassHour.getClassStatus())
				.subject(existingClassHour.getSubject())
				.build();	
	}
	
	

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
						
						DayOfWeek dayOfWeek = date.getDayOfWeek();
						int end=6;
						
					   if(!dayOfWeek.equals(DayOfWeek.MONDAY)) {
						   end=end+(7-dayOfWeek.getValue());
					   }
					   
						// for generating day
						for(int day=1; day<=end; day++) { 
							if(date.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
								date.plusDays(1);
							}
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
	public void generateWeeklyClassHiurs() {
		List<AcademicProgram> programsToBeAutoRepeated = academicProgramRepoistory.findByAutoRepeatScheduledTrue();		{

			if(!programsToBeAutoRepeated.isEmpty())
			{
				programsToBeAutoRepeated.forEach(program->{


					int n=program.getSchool().getSchedule().getClassHoursPerDay() * 6;
					// getting last week class hour
					List<ClassHour> lastWeekClassHours = classHourRepoistory.findLastNRecordsByAcademicProgram(program, n);

					if(!lastWeekClassHours.isEmpty())
					{
						for(int i=lastWeekClassHours.size()-1;i>=0;i--)
						{
							ClassHour existClassHour = lastWeekClassHours.get(i);
							classHourRepoistory.save(mapToNewClassHour(existClassHour));

						}

						System.out.println("this week data generated as per last week data");
					}
					System.out.println("No Last week data present");
				});
				System.out.println("Schedule Successfully Auto Repeated for the Upcoming WEEK.");
			}
			else
				System.out.println("Auto Repeat Schedule : OFF");
		}
	}

	private ClassHour deleteClasshour(ClassHour classHour) {
		classHourRepoistory.delete(classHour);
		return classHour;
	}


	@Override
	public ResponseEntity<ResponseStructure<String>>  writeIntoXlSheet(int programId,ExcelRequestDto excelRequestDto) {

		return academicProgramRepoistory.findById(programId).map(program->{
			if(!program.isDeleted())
			{
				LocalDateTime from=excelRequestDto.getFromDate().atTime(LocalTime.MIDNIGHT);
				LocalDateTime to=excelRequestDto.getToDate().atTime(LocalTime.MIDNIGHT).plusDays(1);
				List<ClassHour> classHours = classHourRepoistory.findAllByAcademicProgramAndBeginsAtBetween(program, from, to);

				if(!classHours.isEmpty())
				{
					XSSFWorkbook writeBook=new XSSFWorkbook();
//					Sheet sheet=writeBook.createSheet();
					org.apache.poi.ss.usermodel.Sheet sheet=writeBook.createSheet();
					int rowNumber=0;
					Row header=sheet.createRow(rowNumber);
					header.createCell(0).setCellValue("Date");
					header.createCell(1).setCellValue("Begin Time");
					header.createCell(2).setCellValue("End Time");
					header.createCell(3).setCellValue("Subject");
					header.createCell(4).setCellValue("Teacher");
					header.createCell(5).setCellValue("Room No");

					DateTimeFormatter timeFormatter=DateTimeFormatter.ofPattern("HH-mm");
					DateTimeFormatter dateFormatter=DateTimeFormatter.ofPattern("YYYY-MM-dd");


					for(ClassHour classHour: classHours)
					{
						Row row=sheet.createRow(++rowNumber);
						row.createCell(0).setCellValue(dateFormatter.format(classHour.getBeginsAt()));
						row.createCell(1).setCellValue(timeFormatter.format(classHour.getBeginsAt()));
						row.createCell(2).setCellValue(timeFormatter.format(classHour.getEndsAt()));

						if(classHour.getSubject()==null)
							row.createCell(3).setCellValue("NOT AVAILABLE");
						else
							row.createCell(3).setCellValue(classHour.getSubject().getSubjectName());

						if(classHour.getUser()==null)
							row.createCell(4).setCellValue("NOT AVAILABLE");
						else
							row.createCell(4).setCellValue(classHour.getUser().getUserName());

						row.createCell(5).setCellValue(classHour.getRoomNo());	
					}

					try {
						writeBook.write(new FileOutputStream(excelRequestDto.getFilePath()+"\\Classhours"+excelRequestDto.getFromDate()+excelRequestDto.getToDate()+".xlsx"));
					} 
					catch (IOException e) {
						e.printStackTrace();
					}

					ResponseStructure<String> structure=new ResponseStructure<>();

					structure.setStatus(HttpStatus.CREATED.value());
					structure.setMessage("Excel Sheet Created Successfully");
					structure.setData("Excel for the program"+programId);



					return new ResponseEntity<ResponseStructure<String>>(structure,HttpStatus.CREATED);

				}
				else
					throw new DataNotExistException("Data Not Present, No class Hours present");
			}
			else
				throw new DataNotExistException("Program  Already Deleted");

		}).orElseThrow(()->new AcademicProgramNotFoundById("Program not present for given program id"));


	}

	@Override
	public ResponseEntity<?> writeToXlSheet(int programId, LocalDate fromDate, LocalDate toDate,
			MultipartFile multipartFile){
		
		return academicProgramRepoistory.findById(programId).map(program->{
			if(!program.isDeleted())
			{
				LocalDateTime from=fromDate.atTime(LocalTime.MIDNIGHT);
				LocalDateTime to=toDate.atTime(LocalTime.MIDNIGHT).plusDays(1);
				List<ClassHour> classHours = classHourRepoistory.findAllByAcademicProgramAndBeginsAtBetween(program, from, to);
				
				DateTimeFormatter timeFormatter=DateTimeFormatter.ofPattern("HH-mm");
				DateTimeFormatter dateFormatter=DateTimeFormatter.ofPattern("YYYY-MM-dd");
				
				XSSFWorkbook writeBook = null;
				try {
					writeBook = new XSSFWorkbook(multipartFile.getInputStream());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(!classHours.isEmpty())
				{
		
					writeBook.forEach(sheet->{
						int rowNumber=0;
						Row header=sheet.createRow(rowNumber);
						header.createCell(0).setCellValue("Date");
						header.createCell(1).setCellValue("Begin Time");
						header.createCell(2).setCellValue("End Time");
						header.createCell(3).setCellValue("Subject");
						header.createCell(4).setCellValue("Teacher");
						header.createCell(5).setCellValue("Room No");
						
						
						for(ClassHour classHour: classHours)
						{
							Row row=sheet.createRow(++rowNumber);
							row.createCell(0).setCellValue(dateFormatter.format(classHour.getBeginsAt()));
							row.createCell(1).setCellValue(timeFormatter.format(classHour.getBeginsAt()));
							row.createCell(2).setCellValue(timeFormatter.format(classHour.getEndsAt()));

							if(classHour.getSubject()==null)
								row.createCell(3).setCellValue("NOT AVAILABLE");
							else
								row.createCell(3).setCellValue(classHour.getSubject().getSubjectName());

							if(classHour.getUser()==null)
								row.createCell(4).setCellValue("NOT AVAILABLE");
							else
								row.createCell(4).setCellValue(classHour.getUser().getUserName());

							row.createCell(5).setCellValue(classHour.getRoomNo());	
						}
						
					});
					ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
					try {
						writeBook.write(outputStream);
						writeBook.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					byte[] byteData = outputStream.toByteArray();

					return ResponseEntity.ok().header("Content Disposition", "attachement; filename="+multipartFile.getOriginalFilename())
							.contentType(MediaType.APPLICATION_OCTET_STREAM)
							.body(byteData);
				}
				else
					throw new DataNotExistException("Data Not Present, No class Hours present");
			}
			else
				throw new DataNotExistException("Program  Already Deleted");
			
			
		}).orElseThrow(()->new AcademicProgramNotFoundById("Program not present for given program id"));
		
	}
}