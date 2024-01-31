package com.school.sba.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.school.sba.enums.UserRole;
import com.school.sba.requestdto.UserRequest;
import com.school.sba.responsedto.UserResponse;
import com.school.sba.utlity.ResponseStructure;

public interface UserService {

//	ResponseEntity<ResponseStructure<UserResponse>> saveUser(UserRequest userRequest);

	ResponseEntity<ResponseStructure<UserResponse>> deleteUser(int userId);

	ResponseEntity<ResponseStructure<UserResponse>> getUserByid(int userId);

	ResponseEntity<ResponseStructure<UserResponse>> addSubjectToTeacher(int userId, int subjectId);

	ResponseEntity<ResponseStructure<UserResponse>> registerAdmin(UserRequest userRequest);

	ResponseEntity<ResponseStructure<UserResponse>> addOtherUser(UserRequest userRequest);

	ResponseEntity<ResponseStructure<List<UserResponse>>> findUserbyRoleInProgram(int programId, UserRole userRole);
   
	void deleteUserPermentaly();
	
}
