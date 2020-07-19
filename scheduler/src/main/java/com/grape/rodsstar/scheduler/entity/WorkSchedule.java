package com.grape.rodsstar.scheduler.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "work_schedule")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class WorkSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "work_schedule_sequence")
    @SequenceGenerator(name = "work_schedule_sequence")
    private Long id;

    @JoinColumn(name = "machine_id", nullable = false)
    @ManyToOne
    private Machine machine;

    @JoinColumn(name = "order_item_id", nullable = false)
    @ManyToOne
    private OrderItem orderItem;

    @Column(name = "start_time")
    private LocalDateTime starTime;
    @Column(name = "end_time")
    private LocalDateTime endTime;

}
