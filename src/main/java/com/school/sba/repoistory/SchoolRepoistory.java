package com.school.sba.repoistory;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.sba.entity.School;

public interface SchoolRepoistory extends JpaRepository<School, Integer> {

	   List<School> findByisDeletedTrue();
}
