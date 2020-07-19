package com.grape.rodsstar.scheduler.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.LocalTime;

@Entity
@Table(name = "work_shift")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkShift {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shift_sequence")
    @SequenceGenerator(name = "shift_sequence")
    private Long id;

    private LocalTime start;

    private LocalTime end;

    public boolean isInShift(LocalTime time){
        return !time.isBefore(start) && time.isBefore(end);
    }

    public boolean isMorningShift(){
        return start.isBefore(LocalTime.NOON);
    }
}
