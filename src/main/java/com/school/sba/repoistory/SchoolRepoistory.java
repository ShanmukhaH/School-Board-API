package com.school.sba.repoistory;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.sba.entity.School;

public interface SchoolRepoistory extends JpaRepository<School, Integer> {

}
