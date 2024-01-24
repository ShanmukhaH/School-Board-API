package com.school.sba.repoistory;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.sba.entity.Schedule;

public interface ScheduleRepoistory extends JpaRepository<Schedule, Integer> {

}
