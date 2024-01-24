package com.school.sba.repoistory;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.sba.entity.Subject;

public interface SubjectRepoistory extends JpaRepository<Subject, Integer> {

	Optional<Subject> findBySubjectName(String name);
}
