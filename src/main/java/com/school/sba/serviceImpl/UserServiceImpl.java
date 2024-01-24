package com.school.sba.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.school.sba.entity.Subject;
import com.school.sba.entity.User;
import com.school.sba.enums.UserRole;
import com.school.sba.exception.InvalidUserException;
import com.school.sba.exception.SubjectNotFoundByIdException;
import com.school.sba.exception.UserNotFoundByIdException;
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
	public ResponseEntity<ResponseStructure<UserResponse>> saveUser(UserRequest userRequest) {


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
	}
	

