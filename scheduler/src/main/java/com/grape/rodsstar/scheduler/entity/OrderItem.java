package com.grape.rodsstar.scheduler.entity;

import com.grape.rodsstar.scheduler.enums.ProductType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "order_item")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_item_sequence")
    @SequenceGenerator(name = "order_item_sequence")
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_type")
    private ProductType productType;

    private Integer quantity;

    private LocalDateTime deadline;

    private Integer profit;

    private Integer penalty;

    @Column(name = "all_profit")
    private Integer allProfit;

    @Column(name = "deducted_penalty")
    private Integer deductedPenalty;

    @Column(name = "all_time")
    private Long allTime;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @OneToMany(mappedBy = "orderItem")
    private List<WorkSchedule> workSchedules;

}
