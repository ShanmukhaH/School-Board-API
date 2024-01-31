package com.school.sba.repoistory;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.sba.entity.ClassHour;
import com.school.sba.entity.User;

public interface ClassHourRepoistory extends JpaRepository<ClassHour, Integer> {

	boolean existsByBeginsAtBetweenAndRoomNo(LocalDateTime beginsAt, LocalDateTime endsAt,int roomNo);

	 List<ClassHour> findByUser(User user);
		
		
}
