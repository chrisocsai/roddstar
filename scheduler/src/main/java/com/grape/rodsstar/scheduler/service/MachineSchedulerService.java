package com.grape.rodsstar.scheduler.service;

import com.grape.rodsstar.scheduler.SchedulerException;
import com.grape.rodsstar.scheduler.csv.ScheduleOutput;
import com.grape.rodsstar.scheduler.entity.JobTime;
import com.grape.rodsstar.scheduler.entity.Machine;
import com.grape.rodsstar.scheduler.entity.OrderItem;
import com.grape.rodsstar.scheduler.entity.WorkShift;
import com.grape.rodsstar.scheduler.entity.WorkSchedule;
import com.grape.rodsstar.scheduler.enums.MachineType;
import com.grape.rodsstar.scheduler.enums.ProductType;
import com.grape.rodsstar.scheduler.repository.MachineRepository;
import com.grape.rodsstar.scheduler.repository.WorkShiftRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class MachineSchedulerService {

    @Autowired
    private WorkShiftService workShiftService;

    @Autowired
    private MachineService machineService;

    public List<WorkSchedule> createWorkSchedule(List<OrderItem> orders){
        return orders.stream()
                .map(this::createWorkSchedule)
                .flatMap(Collection::stream)
                .sorted(Comparator.comparing(WorkSchedule::getStarTime)
                        .thenComparing(ws->ws.getMachine().getName()))
                .collect(Collectors.toList());
    }

    public List<ScheduleOutput> createWorkScheduleOutput(List<WorkSchedule> schedules) {
        return schedules.stream().map(ScheduleOutput::from).collect(Collectors.toList());
    }

    private List<WorkSchedule> createWorkSchedule(OrderItem order){
        List<Machine> machines = machineService.findAll();
        MachineType bottleneckStep = getBottleneckStep(order.getProductType(), machines);
        long bottleneckMachineCount = machineCount(bottleneckStep, machines);
        LocalDateTime start = order.getStartTime();
        List<WorkSchedule> schedules = new ArrayList<>();
        for (MachineType mt: MachineType.values()) {
            List<Machine> sub = machines.stream()
                    .filter(m->m.getMachineType().equals(mt))
                    .sorted(Comparator.comparing(m->m.getJobTime(order.getProductType())))
                    .limit(bottleneckMachineCount)
                    .collect(Collectors.toList());
            schedules.addAll(createWorkSchedule(sub, order.getQuantity(), start, order.getProductType(), order));
            start = start.plus(sub.get(sub.size() - 1).getJobTime(order.getProductType()), ChronoUnit.MINUTES);
            if(start.toLocalTime().isAfter(workShiftService.findAll().get(1).getEnd())){
                start = LocalDateTime.of(start.toLocalDate(), workShiftService.findAll().get(0).getStart()).plus(1, ChronoUnit.DAYS);
            }
        }
        order.setWorkSchedules(schedules);
        return schedules;
    }

    private List<WorkSchedule> createWorkSchedule(List<Machine> machines, long itemCount, LocalDateTime start, ProductType productType, OrderItem order){
        List<WorkSchedule> schedules = new ArrayList<>();
        Map<Machine, Long> maxItemsCreatedInShiftPerMachine = machines.stream().collect(Collectors.toMap(m->m, m->m.howManyItemCreated(productType, start, getCurrentShiftEnd(start))));
        long itemsCreatedInShift = maxItemsCreatedInShiftPerMachine.values().stream().mapToLong(l->l).sum();
        Map<Machine, Long> itemsCreatedInShiftPerMachine = machines.stream().collect(Collectors.toMap(m->m, m->Double.valueOf(Math.floor(maxItemsCreatedInShiftPerMachine.get(m) * 1D / itemsCreatedInShift * Math.min(itemCount, itemsCreatedInShift))).longValue()));
        long allItemCreatedByMachines = itemsCreatedInShiftPerMachine.values().stream().mapToLong(l->l).sum();
        long restItemsCount = Math.min(itemCount, itemsCreatedInShift) - allItemCreatedByMachines;
        if(restItemsCount > 0){
            List<Machine> sub = machines.subList(0, (int) restItemsCount);
            sub.forEach(m->itemsCreatedInShiftPerMachine.put(m, itemsCreatedInShiftPerMachine.get(m) + 1));
        }
        machines.forEach(machine-> schedules.add(
                WorkSchedule.builder()
                        .machine(machine)
                        .starTime(start)
                        .orderItem(order)
                        .endTime(getEndTime(start, itemsCreatedInShiftPerMachine.get(machine) * machine.getJobTime(productType)))
                        .build()));
        if(itemCount > itemsCreatedInShift){
            schedules.addAll(createWorkSchedule(machines, itemCount - itemsCreatedInShift, getNextShiftStart(start), productType, order));
        }
        return schedules;
    }

    private LocalDateTime getEndTime(LocalDateTime start, Long duration){
        return start.plus(duration, ChronoUnit.MINUTES);
    }

    private WorkShift getCurrentShift(LocalDateTime start){
        LocalTime startTime = start.toLocalTime();
        return workShiftService.findAll().stream().filter(s->s.isInShift(startTime)).findFirst().orElseThrow(SchedulerException::new);
    }

    private LocalDateTime getCurrentShiftEnd(LocalDateTime start){
        WorkShift current = getCurrentShift(start);
        return LocalDateTime.of(start.toLocalDate(), current.getEnd());
    }

    private LocalDateTime getNextShiftStart(LocalDateTime start){
        WorkShift current = getCurrentShift(start);
        if(current.isMorningShift()){
            return LocalDateTime.of(start.toLocalDate(), workShiftService.findAll().get(1).getStart());
        } else {
            return LocalDateTime.of(start.toLocalDate(), workShiftService.findAll().get(0).getStart()).plus(1, ChronoUnit.DAYS);
        }
    }

    private MachineType getBottleneckStep(ProductType productType, List<Machine> machines){
        Map<MachineType, Double> averageWorkTimePerMachine = Arrays.stream(MachineType.values()).collect(Collectors.toMap(mt->mt, mt -> averageWorkTime(productType, mt, machines)));
        return averageWorkTimePerMachine.entrySet().stream().max(Map.Entry.comparingByValue()).orElseThrow(SchedulerException::new).getKey();
    }

    private Double averageWorkTime(ProductType productType, MachineType machineType, List<Machine> machines){
        return machines.stream()
                .filter(m->m.getMachineType().equals(machineType))
                .map(Machine::getJobTimes)
                .flatMap(Collection::stream)
                .filter(jt->jt.getProductType().equals(productType))
                .mapToInt(JobTime::getValue)
                .average()
                .orElseThrow(SchedulerException::new);
    }

    private Long machineCount(MachineType machineType, List<Machine> machines){
        return machines.stream().filter(m->m.getMachineType().equals(machineType)).count();
    }
}
