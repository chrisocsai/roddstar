package com.grape.rodsstar.scheduler.entity;

import com.grape.rodsstar.scheduler.enums.MachineType;
import com.grape.rodsstar.scheduler.enums.ProductType;
import com.grape.rodsstar.scheduler.SchedulerException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;

@Entity
@Table(name = "machine")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Machine {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "machine_sequence")
    @SequenceGenerator(name = "machine_sequence")
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "machine_type")
    private MachineType machineType;

    @OneToMany(mappedBy = "machine", fetch = FetchType.EAGER)
    private Set<JobTime> jobTimes;

    @OneToMany(mappedBy = "machine")
    private Set<WorkSchedule> workSchedules;

    public Integer getJobTime(ProductType productType){
        return getJobTimes().stream()
                .filter(jt->jt.getProductType().equals(productType))
                .findFirst()
                .orElseThrow(SchedulerException::new)
                .getValue();
    }

    public Long howManyItemCreated(ProductType productType, LocalDateTime start, LocalDateTime end){
        Long machineWorkTimeInMinutes = start.until(end, ChronoUnit.MINUTES);
        return machineWorkTimeInMinutes / getJobTime(productType);
    }
}
