package com.grape.rodsstar.scheduler.service;

import com.grape.rodsstar.scheduler.entity.OrderItem;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@AllArgsConstructor
@Component
public class OrderComparator implements Comparator<OrderItem> {

    @Autowired
    private final OrderService orderService;

    @Override
    public int compare(OrderItem o1, OrderItem o2) {
        OrderItem _o1 = from(o1);
        OrderItem _o2 = from(o2);
        orderService.calculate(_o1, o2.getEndTime());
        orderService.calculate(_o2, o1.getEndTime());
        Integer profit1 = o1.getAllProfit() + _o2.getAllProfit();
        Integer profit2 = o2.getAllProfit() + _o1.getAllProfit();
        return profit2.compareTo(profit1);
    }

    public OrderItem from(OrderItem other){
        return OrderItem.builder()
                .name(other.getName())
                .productType(other.getProductType())
                .quantity(other.getQuantity())
                .deadline(other.getDeadline())
                .profit(other.getProfit())
                .penalty(other.getPenalty())
                .allProfit(other.getAllProfit())
                .deductedPenalty(other.getDeductedPenalty())
                .allTime(other.getAllTime())
                .startTime(other.getStartTime())
                .endTime(other.getEndTime())
                .build();
    }
}
