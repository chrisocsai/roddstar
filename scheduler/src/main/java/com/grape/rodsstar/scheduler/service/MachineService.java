package com.grape.rodsstar.scheduler.service;

import com.grape.rodsstar.scheduler.entity.Machine;
import com.grape.rodsstar.scheduler.repository.MachineRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class MachineService {

    @Autowired
    private MachineRepository machineRepository;

    @Cacheable(value = "findAllMachine")
    public List<Machine> findAll(){
        return machineRepository.findAll();
    }
}
