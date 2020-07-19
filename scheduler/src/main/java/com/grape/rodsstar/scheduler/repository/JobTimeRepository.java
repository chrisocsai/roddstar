package com.grape.rodsstar.scheduler.repository;

import com.grape.rodsstar.scheduler.entity.JobTime;
import com.grape.rodsstar.scheduler.entity.Machine;
import com.grape.rodsstar.scheduler.enums.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobTimeRepository extends JpaRepository<JobTime, Long> {
}
