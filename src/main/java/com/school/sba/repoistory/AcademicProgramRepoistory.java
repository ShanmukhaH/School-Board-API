package com.school.sba.repoistory;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.sba.entity.AcademicProgram;

public interface AcademicProgramRepoistory extends JpaRepository<AcademicProgram, Integer> {

}
