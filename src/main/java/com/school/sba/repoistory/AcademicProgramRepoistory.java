package com.school.sba.repoistory;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.School;
import com.school.sba.entity.User;

public interface AcademicProgramRepoistory extends JpaRepository<AcademicProgram, Integer> {

	List<AcademicProgram> findByisDeletedTrue();
    List<AcademicProgram> findBySchool(School school);
	List<AcademicProgram> findByAutoRepeatScheduledTrue();
}
