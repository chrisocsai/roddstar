package com.grape.rodsstar.scheduler.service;

import com.grape.rodsstar.scheduler.entity.WorkShift;
import com.grape.rodsstar.scheduler.repository.WorkShiftRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class WorkShiftService {

    @Autowired
    private WorkShiftRepository workShiftRepository;
/*
    private List<WorkShift> all;

    public List<WorkShift> findAll(){
        if(all == null){
            all = workShiftRepository.findAll();
        }
        return all;
    }
*/
    @Cacheable(value = "getAllWorkShift")
    public List<WorkShift> findAll(){
        return workShiftRepository.findAll();
    }
}
