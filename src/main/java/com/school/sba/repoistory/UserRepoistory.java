package com.school.sba.repoistory;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.sba.entity.User;
import com.school.sba.enums.UserRole;

public interface  UserRepoistory extends JpaRepository<User, Integer> {

	boolean existsByUserRole(UserRole admin);
	
	 User  findByUserRole(UserRole admin);

	Optional<User> findByUserName(String userName);

	List<User> findByUserRoleAndPrograms_ProgramId(UserRole userRole,int programId);

	List<User> findByisDeletedTrue();
	
    List<User> findByUserRoleNot(UserRole userRole);
}
