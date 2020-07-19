package com.grape.rodsstar.scheduler.service;

import com.grape.rodsstar.scheduler.SchedulerException;
import com.grape.rodsstar.scheduler.csv.OrderInput;
import com.grape.rodsstar.scheduler.csv.OrderOutput;
import com.grape.rodsstar.scheduler.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class SchedulerService {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderComparator orderComparator;

    public List<OrderItem> createSchedule(List<OrderInput> input, LocalDateTime start){
        List<OrderItem> orders = createOrders(input);
        return schedule(orders, start);
    }

    public List<OrderOutput> createOrderOutput(List<OrderItem> orders){
        return orders.stream().map(OrderOutput::from).collect(Collectors.toList());
    }

    private List<OrderItem> createOrders(List<OrderInput> input){
        return input.stream().map(orderService::create).collect(Collectors.toList());
    }

    private List<OrderItem> schedule(List<OrderItem> orders, LocalDateTime start){
        List<OrderItem> result = new ArrayList<>();
        OrderItem best = findBest(orders, start);
        result.add(best);
        orders.remove(best);
        if (!orders.isEmpty()){
            result.addAll(schedule(orders, best.getEndTime()));
        }
        return result;
    }

    private OrderItem findBest(List<OrderItem> orders, LocalDateTime start){
        return orders.stream()
                .map(order -> orderService.calculate(order, start))
                .min(orderComparator)
                .orElseThrow(SchedulerException::new);
    }

}
