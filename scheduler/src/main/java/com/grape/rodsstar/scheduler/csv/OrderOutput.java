package com.grape.rodsstar.scheduler.csv;

import com.grape.rodsstar.scheduler.csv.converter.LocalDateTimeConverter;
import com.grape.rodsstar.scheduler.csv.converter.MoneyAmountConverter;
import com.grape.rodsstar.scheduler.entity.OrderItem;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvCustomBindByPosition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class OrderOutput {
    @CsvBindByName(column = "Megrendelésszám")
    @CsvBindByPosition(position = 0)
    private String id;
    @CsvBindByName(column = "Profit összesen")
    @CsvCustomBindByPosition(position = 1, converter = MoneyAmountConverter.class)
    private Integer profit;
    @CsvBindByName(column = "Levont kötbér")
    @CsvCustomBindByPosition(position = 2, converter = MoneyAmountConverter.class)
    private Integer penalty;
    @CsvBindByName(column = "Munka megkezdése")
    @CsvCustomBindByPosition(position = 3, converter = LocalDateTimeConverter.class)
    private LocalDateTime startDate;
    @CsvBindByName(column = "Készre jelentés ideje")
    @CsvCustomBindByPosition(position = 4, converter = LocalDateTimeConverter.class)
    private LocalDateTime endDate;
    @CsvBindByName(column = "Megrendelés eredeti határideje")
    @CsvCustomBindByPosition(position = 5, converter = LocalDateTimeConverter.class)
    private LocalDateTime termDate;

    public static OrderOutput from(OrderItem order){
        return OrderOutput.builder()
                .id(order.getName())
                .startDate(order.getStartTime())
                .endDate(order.getEndTime())
                .termDate(order.getDeadline())
                .profit(order.getAllProfit())
                .penalty(order.getDeductedPenalty())
                .build();
    }
}
