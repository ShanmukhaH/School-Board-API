package com.school.sba.repoistory;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.sba.entity.User;

public interface  UserRepoistory extends JpaRepository<User, Integer> {

}
