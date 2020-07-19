package com.grape.rodsstar.scheduler.service;

import com.grape.rodsstar.scheduler.csv.OrderInput;
import com.grape.rodsstar.scheduler.entity.OrderItem;
import com.grape.rodsstar.scheduler.enums.MachineType;
import com.grape.rodsstar.scheduler.repository.MachineRepository;
import com.grape.rodsstar.scheduler.repository.OrderItemRepository;
import com.grape.rodsstar.scheduler.repository.WorkShiftRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;

@Service
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class OrderService {

    @Autowired
    private MachineService machineService;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CalculateService calculateService;

    @Autowired
    private WorkShiftService workShiftService;

    public OrderItem create(OrderInput input){
        OrderItem order = OrderItem.builder()
                .name(input.getId())
                .productType(input.getProductType())
                .quantity(input.getQuantity())
                .deadline(input.getTermDate())
                .profit(input.getProfitPerUnit())
                .penalty(input.getPenaltyPerDay())
                .build();
        order.setAllTime(calculateTotalTime(order));
        return order;
    }

    public OrderItem calculate(OrderItem order, LocalDateTime start){
        order.setStartTime(start);
        order.setEndTime(calculateExpectedEnd(order, start));
        order.setDeductedPenalty(calculateExpectedPenalty(order, order.getEndTime()));
        order.setAllProfit(calculateExpectedProfit(order) - order.getDeductedPenalty());
        return order;
    }

    public LocalDateTime getLastOrderEnd(){
        return orderItemRepository.findAll().stream()
                .max(Comparator.comparing(OrderItem::getId))
                .orElse(OrderItem.builder().endTime(LocalDateTime.now()).build())
                .getEndTime();
    }

    private long calculateTotalTime(OrderItem input) {
        double totalTimeForOneUnit = calculateService.calculateTotalTimeForOneUnit(input.getProductType());
        double slowestStepTime = calculateService.getSlowestStepTime(input.getProductType());
        double otherStepsTime = totalTimeForOneUnit - slowestStepTime;
        MachineType slowestMachineType = calculateService.getSlowestMachine(input.getProductType());
        long slowestMachineCount = machineService.findAll().stream().filter(m->m.getMachineType().equals(slowestMachineType)).count();
        double totalCraftTime = slowestStepTime * input.getQuantity() + otherStepsTime * slowestMachineCount;
        return (long) Math.ceil(totalCraftTime);
    }

    private LocalDateTime calculateExpectedEnd(OrderItem input, LocalDateTime start) {
        long totalTime = calculateTotalTime(input);
        return calculateExpectedEnd(start, totalTime);
    }

    private LocalDateTime calculateExpectedEnd(LocalDateTime start, long remainingTime){
        LocalTime dayEnd = workShiftService.findAll().get(1).getEnd();
        long elapsed = start.toLocalTime().until(dayEnd, ChronoUnit.MINUTES);
        if(elapsed >= remainingTime){
            return start.plusMinutes(remainingTime);
        } else {
            return calculateExpectedEnd(LocalDateTime.of(start.plusDays(1).toLocalDate(), workShiftService.findAll().get(0).getStart()), remainingTime - elapsed);
        }
    }

    private Integer calculateExpectedProfit(OrderItem input) {
        return input.getQuantity() * input.getProfit();
    }

    private Integer calculateExpectedPenalty(OrderItem input, LocalDateTime expectedEnd) {
        if(!input.getDeadline().isBefore(expectedEnd)){
            return 0;
        }
        return calculateLateInDays(input.getDeadline(), expectedEnd) * input.getPenalty();
    }

    private Integer calculateLateInDays(LocalDateTime termDate, LocalDateTime expectedEnd){
        long lateInMinutes = termDate.until(expectedEnd, ChronoUnit.MINUTES);
        double lateInDays = lateInMinutes / 60D / 24D;
        return (int) Math.ceil(lateInDays);
    }
}
