package com.grape.rodsstar.scheduler.csv;

import com.grape.rodsstar.scheduler.entity.OrderItem;
import com.grape.rodsstar.scheduler.entity.WorkSchedule;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@Builder
public class ScheduleOutput {
    @CsvBindByName(column = "Dátum")
    @CsvDate("yyyy.MM.dd.")
    @CsvBindByPosition(position = 0)
    private LocalDate date;
    @CsvBindByName(column = "Gép")
    @CsvBindByPosition(position = 1)
    private String machine;
    @CsvBindByName(column = "Kezdő időpont")
    @CsvBindByPosition(position = 2)
    private LocalTime startTime;
    @CsvBindByName(column = "Záró időpont")
    @CsvBindByPosition(position = 3)
    private LocalTime endTime;
    @CsvBindByName(column = "Megrendelésszám")
    @CsvBindByPosition(position = 4)
    private String orderId;

    public static ScheduleOutput from(WorkSchedule schedule){
        return ScheduleOutput.builder()
                .date(schedule.getStarTime().toLocalDate())
                .machine(schedule.getMachine().getName())
                .startTime(schedule.getStarTime().toLocalTime())
                .endTime(schedule.getEndTime().toLocalTime())
                .orderId(schedule.getOrderItem().getName())
                .build();
    }
}