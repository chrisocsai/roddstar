package com.grape.rodsstar.scheduler.service;

import com.grape.rodsstar.scheduler.SchedulerException;
import com.grape.rodsstar.scheduler.entity.JobTime;
import com.grape.rodsstar.scheduler.entity.Machine;
import com.grape.rodsstar.scheduler.enums.MachineType;
import com.grape.rodsstar.scheduler.enums.ProductType;
import com.grape.rodsstar.scheduler.repository.JobTimeRepository;
import com.grape.rodsstar.scheduler.repository.MachineRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class CalculateService {

    @Autowired
    private MachineService machineService;

    @Cacheable("calculateTotalTimeForOneUnit")
    public double calculateTotalTimeForOneUnit(ProductType productType){
        return Arrays.stream(MachineType.values()).mapToDouble(mt->calculateTimeForOneUnit(productType, mt)).sum();
    }

    @Cacheable("calculateTimeForOneUnit")
    public Double calculateTimeForOneUnit(ProductType productType, MachineType machineType){
        List<Integer> jobTimes = machineService.findAll().stream()
                .filter(m->m.getMachineType().equals(machineType))
                .map(m->m.getJobTime(productType))
                .collect(Collectors.toList());
        return 1D / jobTimes.stream().mapToDouble(jobTime->1D / jobTime).sum();
    }

    @Cacheable("getSlowestStepTime")
    public double getSlowestStepTime(ProductType productType){
        return Arrays.stream(MachineType.values()).mapToDouble(mt->calculateTimeForOneUnit(productType, mt)).max().orElse(0);
    }

    @Cacheable("getSlowestMachine")
    public MachineType getSlowestMachine(ProductType productType){
        return Arrays.stream(MachineType.values())
                .max(Comparator.comparing(mt -> calculateTimeForOneUnit(productType, mt)))
                .orElseThrow(SchedulerException::new);
    }
}
