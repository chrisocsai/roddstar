package com.grape.rodsstar.scheduler.entity;

import com.grape.rodsstar.scheduler.enums.ProductType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "job_time")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobTime {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "job_time_sequence")
    @SequenceGenerator(name = "job_time_sequence")
    private Long id;

    @JoinColumn(name = "machine_id", nullable = false)
    @ManyToOne
    private Machine machine;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_type")
    private ProductType productType;

    private Integer value;

}
