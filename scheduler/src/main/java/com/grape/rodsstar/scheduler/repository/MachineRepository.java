package com.grape.rodsstar.scheduler.repository;

import com.grape.rodsstar.scheduler.entity.Machine;
import com.grape.rodsstar.scheduler.enums.MachineType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MachineRepository extends JpaRepository<Machine, Long> {
}
