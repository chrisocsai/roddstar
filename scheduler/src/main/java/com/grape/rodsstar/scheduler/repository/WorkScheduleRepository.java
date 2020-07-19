package com.grape.rodsstar.scheduler.repository;

import com.grape.rodsstar.scheduler.entity.WorkSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Long> {
}
