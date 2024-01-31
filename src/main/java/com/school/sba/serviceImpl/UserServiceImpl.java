package com.school.sba.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.ClassHour;
import com.school.sba.entity.Subject;
import com.school.sba.entity.User;
import com.school.sba.enums.UserRole;
import com.school.sba.exception.AcademicProgramNotFoundById;
import com.school.sba.exception.InvalidUserException;
import com.school.sba.exception.SchoolNotFoundByIDException;
import com.school.sba.exception.SubjectNotFoundByIdException;
import com.school.sba.exception.UserNotFoundByIdException;
import com.school.sba.exception.UserNotFoundbyNameException;
import com.school.sba.repoistory.AcademicProgramRepoistory;
import com.school.sba.repoistory.ClassHourRepoistory;
import com.school.sba.repoistory.SchoolRepoistory;
import com.school.sba.repoistory.SubjectRepoistory;
import com.school.sba.repoistory.UserRepoistory;
import com.school.sba.requestdto.UserRequest;
import com.school.sba.responsedto.UserResponse;
import com.school.sba.service.UserService;
import com.school.sba.utlity.ResponseStructure;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepoistory userRepoistory;

	@Autowired
	private SubjectRepoistory subjectRepoistory;

	@Autowired
	private SchoolRepoistory  schoolRepoistory;
	
	@Autowired
	private ClassHourRepoistory classHourRepoistory;

	@Autowired
	private AcademicProgramRepoistory academicProgramRepoistory;

	@Autowired
	private ResponseStructure<UserResponse> structure;

	@Autowired
	private PasswordEncoder encoder;

	public User mapToUser(UserRequest userRequest) {
		return User.builder()
				.userName(userRequest.getUserName())
				.password(encoder.encode(userRequest.getPassword()))
				.firstName(userRequest.getFirstName())
				.lastName(userRequest.getLastName())
				.contactNo(userRequest.getContactNo())
				.email(userRequest.getEmail())
				.userRole(userRequest.getUserRole())
				.build();
	}

	public UserResponse mapToUserResponse(User user) {
		return UserResponse.builder()
				.userId(user.getUserId())
				.userName(user.getUserName())
				.firstName(user.getFirstName())
				.lastName(user.getLastName())
				.contactNo(user.getContactNo())
				.email(user.getEmail())
				.userRole(user.getUserRole())
				.build();
	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> registerAdmin(UserRequest userRequest) {


		User user = mapToUser(userRequest);
		if(user.getUserRole()==UserRole.ADMIN&&userRepoistory.existsByUserRole(UserRole.ADMIN)) {

			structure.setStatus(HttpStatus.BAD_REQUEST.value());
			structure.setMessage("There should be only one ADMIN to the application");
			return new ResponseEntity<ResponseStructure<UserResponse>>(structure,HttpStatus.BAD_REQUEST);

		}

		userRepoistory.save(user);
		structure.setStatus(HttpStatus.CREATED.value());
		structure.setMessage("User Data Saved Sucessfully");
		structure.setData(mapToUserResponse(user));

		return new ResponseEntity<ResponseStructure<UserResponse>>(structure,HttpStatus.CREATED);

	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> addOtherUser(UserRequest userRequest) 
	{
		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		User adminuser = userRepoistory.findByUserName(username).orElseThrow(()-> new UserNotFoundbyNameException("Not able to find user"));

		if(adminuser.getSchool()!=null) {
			//		School school = schoolRepoistory.findById(user.getSchool().getSchoolId()).orElseThrow(()->new SchoolNotFoundByIDException("School not found by id"));
			User user = mapToUser(userRequest);

			if(user.getUserRole().equals(UserRole.TEACHER) || user.getUserRole().equals(UserRole.STUDENT))
			{
				user.setSchool(adminuser.getSchool());
				userRepoistory.save(user);

				structure.setStatus(HttpStatus.CREATED.value());
				structure.setMessage("User Data Saved Sucessfully");
				structure.setData(mapToUserResponse(user));

				return new ResponseEntity<ResponseStructure<UserResponse>>(structure,HttpStatus.CREATED);
			}
			else
				throw new InvalidUserException("Unable to save user, Invalid user role");
		}
		else {
			throw new SchoolNotFoundByIDException("school not present");
		}

	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> deleteUser(int userId) {

		User user = userRepoistory.findById(userId).orElseThrow(()->new UserNotFoundByIdException("User Not Found By id"));

		if(user.isDeleted()==false)
			user.setDeleted(true);
		userRepoistory.save(user);

		structure.setStatus(HttpStatus.FOUND.value());
		structure.setMessage("User Data Delete");
		structure.setData(mapToUserResponse(user));


		return new ResponseEntity<ResponseStructure<UserResponse>>(structure,HttpStatus.OK);
	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> getUserByid(int userId) {
		User user = userRepoistory.findById(userId).orElseThrow(()->new UserNotFoundByIdException("User Not Found"));

		structure.setStatus(HttpStatus.FOUND.value());
		structure.setMessage("User Data Found");
		structure.setData(mapToUserResponse(user));
		return new ResponseEntity<ResponseStructure<UserResponse>>(structure,HttpStatus.FOUND);
	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> addSubjectToTeacher(int userId, int subjectId) {

		User user = userRepoistory.findById(userId).orElseThrow(()->new UserNotFoundByIdException("User not Found For given Id"));
		Subject subject = subjectRepoistory.findById(subjectId).orElseThrow(()->new SubjectNotFoundByIdException("Subjects Not Found For given id"));

		if(user.getUserRole().equals(UserRole.TEACHER)) {

			user.setSubject(subject);
			userRepoistory.save(user);
			structure.setStatus(HttpStatus.CREATED.value());
			structure.setMessage("upadted Sucesfully");
			structure.setData(mapToUserResponse(user));
			return new ResponseEntity<ResponseStructure<UserResponse>>(structure,HttpStatus.CREATED);
		}else {
			throw new InvalidUserException("Invalid User");
		}

	}

	@Override
	public ResponseEntity<ResponseStructure<List<UserResponse>>> findUserbyRoleInProgram(int programId,
			UserRole userRole) {

		ResponseStructure<List<UserResponse>> structure=new ResponseStructure<>();

		if(userRole.equals(UserRole.ADMIN)) {

			throw new InvalidUserException("No need to fetch Admin Details");
		}
		AcademicProgram program = academicProgramRepoistory.findById(programId).orElseThrow(()->new AcademicProgramNotFoundById("Academic Program Not Found for Given Id"));

		List<User> userList= userRepoistory.findByUserRoleAndPrograms_ProgramId(userRole,programId);

		if(!userList.isEmpty()) {

			ArrayList<UserResponse> userslist=new ArrayList<UserResponse>();

			for(User user:userList) {

				UserResponse response = mapToUserResponse(user);
				userslist.add(response);
			}

			structure.setStatus(HttpStatus.FOUND.value());
			structure.setMessage(" Data Found Sucessfully");
			structure.setData(userslist);

			return new ResponseEntity<ResponseStructure<List<UserResponse>>>(structure,HttpStatus.FOUND);
		}else {

			throw new InvalidUserException("Users Not theree!!!!");

		}


	}
	public void deleteUserPermentaly() {
		
		List<User> users = userRepoistory.findByisDeletedTrue();
		for(User user:users) {
			if(user.getPrograms()!=null) {
				for (AcademicProgram academicProgram : user.getPrograms()) {
					academicProgram.getUsers().remove(user);
				}
				academicProgramRepoistory.saveAll(user.getPrograms());
			}
			
			List<ClassHour> classHours = classHourRepoistory.findByUser(user);		
			for (ClassHour classHour : classHours) {
				classHour.setUser(null);
			}
			classHourRepoistory.saveAll(classHours);
		}
				
		userRepoistory.deleteInBatch(users);
	}


}


