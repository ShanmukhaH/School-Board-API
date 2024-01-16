package com.school.sba.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.User;
import com.school.sba.enums.UserRole;
import com.school.sba.exception.DuplicateEntryException;
import com.school.sba.exception.InvalidUserException;
import com.school.sba.exception.UserNotFoundById;
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
	private ResponseStructure<UserResponse> structure;
	
	static boolean admini=false;
	
	public User mapToUser(UserRequest userRequest) {
		return User.builder()
				.userName(userRequest.getUserName())
				.password(userRequest.getPassword())
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
		 
		if(userRequest.getUserRole()==UserRole.ADMIN) {
			if(admini==false) {
				admini=true;
				try {
					User user = userRepoistory.save(mapToUser(userRequest));
				    structure.setStatus(HttpStatus.CREATED.value());
				    structure.setMessage("User Data Saved Sucessfully");
				    structure.setData(mapToUserResponse(user));
				
			}
				catch(Exception ex) {
					throw new DuplicateEntryException("Change UserName and Email");
				}
				
		}
			else {
				throw new InvalidUserException("Only one Admini Can Be Allowed");
			}
		}
		else {
				try {
					User user = userRepoistory.save(mapToUser(userRequest));
				    structure.setStatus(HttpStatus.CREATED.value());
				    structure.setMessage("User Data Saved Sucessfully");
				    structure.setData(mapToUserResponse(user));
				}
				catch(Exception ex) {
				
					throw new DuplicateEntryException("Change UserName and Email");
				}
			}
	
		return new ResponseEntity<ResponseStructure<UserResponse>>(structure,HttpStatus.CREATED);

}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> deleteUser(int userId) {
		
		User user = userRepoistory.findById(userId).orElseThrow(()->new UserNotFoundById("User Not Found By id"));
		user.setDeleted(true);
		userRepoistory.save(user);
		
		structure.setStatus(HttpStatus.FOUND.value());
		structure.setMessage("User Data Delete");
		structure.setData(mapToUserResponse(user));
		
		
		return new ResponseEntity<ResponseStructure<UserResponse>>(structure,HttpStatus.OK);
	}

	@Override
	public ResponseEntity<ResponseStructure<UserResponse>> getUserByid(int userId) {
		User user = userRepoistory.findById(userId).orElseThrow(()->new UserNotFoundById("User Not Found"));
		
		structure.setStatus(HttpStatus.FOUND.value());
		structure.setMessage("User Data Found");
		structure.setData(mapToUserResponse(user));
		return new ResponseEntity<ResponseStructure<UserResponse>>(structure,HttpStatus.FOUND);
	}
}
